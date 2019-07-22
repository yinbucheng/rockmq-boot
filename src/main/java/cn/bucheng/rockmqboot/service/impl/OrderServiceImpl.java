package cn.bucheng.rockmqboot.service.impl;

import cn.bucheng.rockmqboot.entity.ItemEntity;
import cn.bucheng.rockmqboot.entity.ItemStockEntity;
import cn.bucheng.rockmqboot.entity.OrderEntity;
import cn.bucheng.rockmqboot.entity.PromoEntity;
import cn.bucheng.rockmqboot.exception.BusinessError;
import cn.bucheng.rockmqboot.exception.BusinessException;
import cn.bucheng.rockmqboot.mapper.ItemMapper;
import cn.bucheng.rockmqboot.mapper.ItemStockMapper;
import cn.bucheng.rockmqboot.mapper.OrderMapper;
import cn.bucheng.rockmqboot.mapper.PromoMapper;
import cn.bucheng.rockmqboot.service.OrderService;
import cn.bucheng.rockmqboot.vo.ItemModel;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private PromoMapper promoMapper;

    @Autowired
    private ItemStockMapper itemStockMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void createOrder(Long itemId, Long userId, Integer amount, Integer price, Long promoId) {
        //获取是否已经售空
        Object soldFlag = redisTemplate.opsForHash().get("item_sold_out_promo", "item_" + itemId);
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
                redisTemplate.opsForHash().putIfAbsent("item_sold_out_promo", "item_" + itemId, "sold out");
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
}
