package cn.bucheng.rockmqboot.service.impl;

import cn.bucheng.rockmqboot.constant.PromoActiveConstant;
import cn.bucheng.rockmqboot.constant.PromoRedisConstant;
import cn.bucheng.rockmqboot.entity.ItemStockEntity;
import cn.bucheng.rockmqboot.entity.PromoEntity;
import cn.bucheng.rockmqboot.exception.BusinessError;
import cn.bucheng.rockmqboot.exception.BusinessException;
import cn.bucheng.rockmqboot.mapper.ItemStockMapper;
import cn.bucheng.rockmqboot.mapper.PromoMapper;
import cn.bucheng.rockmqboot.service.PromoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;

/**
 * @author ：yinchong
 * @create ：2019/7/22 19:32
 * @description：
 * @modified By：
 * @version:
 */
@Service
public class PromoServiceImpl extends ServiceImpl<PromoMapper, PromoEntity> implements PromoService {

    @Autowired
    private ItemStockMapper itemStockMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public void pushPromo(long promoId) {
        PromoEntity promoEntity = baseMapper.selectById(promoId);
        if (ObjectUtils.isEmpty(promoEntity)) {
            throw new BusinessException(BusinessError.CAN_NOT_FIND_RECORD.getMessage());
        }
        if (promoEntity.getEndTime().before(new Date())) {
            throw new BusinessException(BusinessError.NO_AVAILABLE_OPERATION.getMessage());
        }

        //将数据刷新到redis中一般是5倍关系
        ItemStockEntity stockEntity = new ItemStockEntity();
        stockEntity.setItemId(promoEntity.getItemId());
        stockEntity = itemStockMapper.selectOne(stockEntity);
        int stockNum = stockEntity.getStock();
        //清除掉售空标记
        redisTemplate.opsForHash().delete(PromoRedisConstant.PROMO_SOLD_OUT, PromoRedisConstant.ITEM_KEY + promoEntity.getItemId());
        //将商品的库存数量存放到redis中
        redisTemplate.opsForHash().put(PromoRedisConstant.PROMO_ITEM_STOCK, PromoRedisConstant.ITEM_KEY + promoEntity.getItemId(), stockNum+"");
    }
}