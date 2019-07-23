package cn.bucheng.rockmqboot.service.impl;

import cn.bucheng.rockmqboot.constant.PromoRedisConstant;
import cn.bucheng.rockmqboot.constant.StockLogConstant;
import cn.bucheng.rockmqboot.entity.*;
import cn.bucheng.rockmqboot.exception.BusinessError;
import cn.bucheng.rockmqboot.exception.BusinessException;
import cn.bucheng.rockmqboot.mapper.*;
import cn.bucheng.rockmqboot.mq.RockMQProducer;
import cn.bucheng.rockmqboot.service.ItemService;
import cn.bucheng.rockmqboot.service.OrderService;
import cn.bucheng.rockmqboot.vo.ItemModel;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.UUID;

/**
 * @author ：yinchong
 * @create ：2019/7/22 14:40
 * @description：
 * @modified By：
 * @version:
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private PromoMapper promoMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemStockMapper itemStockMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private StockLogMapper stockLogMapper;
    @Autowired
    private RockMQProducer rockMQProducer;


    @Override
    @Transactional
    public void createOrder(Long itemId, Long userId, Integer amount, Integer price, Long promoId) {

        //获取是否已经售空
        Object soldFlag = redisTemplate.opsForHash().get(PromoRedisConstant.PROMO_SOLD_OUT, PromoRedisConstant.ITEM_KEY + itemId);
        if (!ObjectUtils.isEmpty(soldFlag)) {
            throw new BusinessException(BusinessError.NO_AVAILABLE_RECORD.getMessage());
        }

        ItemEntity itemEntity = itemMapper.selectById(itemId);
        if (ObjectUtils.isEmpty(itemEntity)) {
            throw new BusinessException(BusinessError.CAN_NOT_FIND_RECORD.getMessage());
        }

        PromoEntity promoEntity = promoMapper.selectById(promoId);
        if (ObjectUtils.isEmpty(promoEntity)) {
            throw new BusinessException(BusinessError.CAN_NOT_FIND_RECORD.getMessage());
        }

        ItemStockEntity itemStock = new ItemStockEntity();
        itemStock.setItemId(itemId);
        itemStock = itemStockMapper.selectOne(itemStock);
        if (ObjectUtils.isEmpty(itemStock)) {
            throw new BusinessException(BusinessError.CAN_NOT_FIND_RECORD.getMessage());
        }

        if (amount <= 0 || amount > itemStock.getStock() || itemStock.getStock() == 0) {
            //如果卖完添加售空标记
            if (itemStock.getStock() == 0) {
                redisTemplate.opsForHash().putIfAbsent(PromoRedisConstant.PROMO_SOLD_OUT, PromoRedisConstant.ITEM_KEY + itemId, "sold out");
            }
            throw new BusinessException(BusinessError.INVALID_NUMBER.getMessage());
        }

        //生成订单
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        OrderEntity entity = new OrderEntity();
        entity.setId(traceId);
        entity.setAmount(amount);
        entity.setItemId(itemId);
        entity.setItemPrice(itemEntity.getPrice());
        entity.setOrderPrice(promoEntity.getPromoItemPrice());
        entity.setUserId(userId);
        entity.setPromoId(promoId);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        baseMapper.insert(entity);

        //扣除掉库存
        int row = itemStockMapper.decrementStock(itemStock.getId(), amount);
        if (row <= 0) {
            throw new BusinessException(BusinessError.EXECUTE_SQL_FAIL.getMessage());
        }

        //增加销售数量
        row = itemMapper.incrementSales(itemId, amount);
        if (row <= 0) {
            throw new BusinessException(BusinessError.EXECUTE_SQL_FAIL.getMessage());
        }
    }


    @Override
    @Transactional
    public void createNewOrder(Long itemId, Long userId, Integer amount, Long promoId) {
        //获取是否已经售空
        Object soldFlag = redisTemplate.opsForHash().get(PromoRedisConstant.PROMO_SOLD_OUT, PromoRedisConstant.ITEM_KEY + itemId);
        if (!ObjectUtils.isEmpty(soldFlag)) {
            throw new BusinessException(BusinessError.NO_AVAILABLE_RECORD.getMessage());
        }
        //加入库存流水
        long stockLogId = insertStockLogRecord(itemId, userId, amount, promoId);
        //发送消息到rockmq中
        boolean flag = rockMQProducer.txAsyncReduceStock(itemId, userId, promoId, amount, stockLogId);
        if (!flag) {
            throw new BusinessException(BusinessError.UNKNOW_ERROR.getMessage());
        }
    }

    //真正执行更新库的操作
    @Override
    public void doCreateNewOrder(Long itemId, Long userId, Integer amount, Long promoId, Long stockLogId) {

        StockLogEntity stockLogEntity = stockLogMapper.selectById(stockLogId);
        if (null == stockLogEntity) {
            throw new BusinessException(BusinessError.NO_AVAILABLE_RECORD.getMessage());
        }
        //添加事务执行拦截
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

            //这里只会在事务提交才会进入到这里
            @Override
            public void afterCommit() {
                super.afterCommit();
            }

            @Override
            public void afterCompletion(int status) {
                //status 0 表示成功 1表示回滚
                super.afterCompletion(status);
                stockLogEntity.setUpdateTime(new Date());
                if (status == 0) {
                    stockLogEntity.setStatus(StockLogConstant.COMMIT);
                } else {
                    stockLogEntity.setStatus(StockLogConstant.ROLLBACK);
                }
                stockLogMapper.updateById(stockLogEntity);
            }
        });

        ItemModel itemModel = itemService.findItem(itemId);
//        ItemEntity itemModel = itemMapper.selectById(itemId);
        if (ObjectUtils.isEmpty(itemModel)) {
            throw new BusinessException(BusinessError.CAN_NOT_FIND_RECORD.getMessage());
        }


        PromoEntity promoEntity = promoMapper.selectById(promoId);
        if (ObjectUtils.isEmpty(promoEntity)) {
            throw new BusinessException(BusinessError.CAN_NOT_FIND_RECORD.getMessage());
        }

        //减掉redis缓存中的数据
        Long count = redisTemplate.opsForHash().increment(PromoRedisConstant.PROMO_ITEM_STOCK, PromoRedisConstant.ITEM_KEY + itemId, -amount);
        if (count < 0) {
            redisTemplate.opsForHash().increment(PromoRedisConstant.PROMO_ITEM_STOCK, PromoRedisConstant.ITEM_KEY + itemId, amount);
            throw new BusinessException(BusinessError.NO_AVAILABLE_RECORD.getMessage());
        } else if (count == 0) {
            redisTemplate.opsForHash().put(PromoRedisConstant.PROMO_SOLD_OUT, PromoRedisConstant.ITEM_KEY + itemId, "sold out");
        }

        //生成订单
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        OrderEntity entity = new OrderEntity();
        entity.setId(traceId);
        entity.setAmount(amount);
        entity.setItemId(itemId);
        entity.setItemPrice(itemModel.getPrice());
        entity.setOrderPrice(promoEntity.getPromoItemPrice());
        entity.setUserId(userId);
        entity.setPromoId(promoId);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        baseMapper.insert(entity);
    }

    private long insertStockLogRecord(Long itemId, Long userId, Integer amount, Long promoId) {
        StockLogEntity stockLogEntity = new StockLogEntity();
        stockLogEntity.setAmount(amount);
        stockLogEntity.setItemId(itemId);
        stockLogEntity.setUserId(userId);
        stockLogEntity.setStatus(StockLogConstant.WAIT_EXECUTE);
        stockLogEntity.setPromoId(promoId);
        stockLogEntity.setUpdateTime(new Date());
        stockLogEntity.setCreateTime(new Date());
        stockLogMapper.insert(stockLogEntity);
        return stockLogEntity.getId();
    }


}
