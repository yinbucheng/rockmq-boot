package cn.bucheng.rockmqboot.service.impl;

import cn.bucheng.rockmqboot.constant.PromoActiveConstant;
import cn.bucheng.rockmqboot.entity.ItemEntity;
import cn.bucheng.rockmqboot.entity.ItemStockEntity;
import cn.bucheng.rockmqboot.entity.PromoEntity;
import cn.bucheng.rockmqboot.exception.BusinessError;
import cn.bucheng.rockmqboot.exception.BusinessException;
import cn.bucheng.rockmqboot.mapper.ItemMapper;
import cn.bucheng.rockmqboot.mapper.ItemStockMapper;
import cn.bucheng.rockmqboot.mapper.PromoMapper;
import cn.bucheng.rockmqboot.service.CacheService;
import cn.bucheng.rockmqboot.service.ItemService;
import cn.bucheng.rockmqboot.vo.ItemModel;
import cn.bucheng.rockmqboot.vo.PromoModel;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/7/22 14:35
 * @description：
 * @modified By：
 * @version:
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class ItemServiceImpl extends ServiceImpl<ItemMapper, ItemEntity> implements ItemService {
    @Autowired
    private PromoMapper promoMapper;
    @Autowired
    private ItemStockMapper itemStockMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CacheService cacheService;

    @Override
    public List<ItemEntity> listItems() {
        return baseMapper.selectList(new EntityWrapper<>());
    }

    @Override
    public ItemModel findItem(Long id) {
        ItemModel item_data = (ItemModel) cacheService.getFromCommonCache("item_data_" + id);
        if (!Objects.isNull(item_data)) {
            return item_data;
        }
        item_data = (ItemModel) redisTemplate.opsForValue().get("item_data_" + id);
        if (!Objects.isNull(item_data)) {
            cacheService.setCommonCache("item_data_" + id, item_data);
            return item_data;
        }
        ItemModel model = new ItemModel();
        ItemEntity itemEntity = baseMapper.selectById(id);
        if (Objects.isNull(itemEntity)) {
            throw new BusinessException(BusinessError.CAN_NOT_FIND_RECORD.getMessage());
        }
        BeanUtils.copyProperties(itemEntity, model);
        //获取库存
        ItemStockEntity stockEntity = new ItemStockEntity();
        stockEntity.setId(itemEntity.getId());
        stockEntity = itemStockMapper.selectOne(stockEntity);
        if (!Objects.isNull(stockEntity)) {
            model.setStock(stockEntity.getStock());
        }

        //获取促销
        Wrapper<PromoEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("item_id", itemEntity.getId());
        List<PromoEntity> promoEntities = promoMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(promoEntities)) {
            PromoEntity promoEntity = promoEntities.get(0);
            PromoModel promoModel = new PromoModel();
            Date now = new Date();
            BeanUtils.copyProperties(promoEntity, promoModel);
            promoModel.setItemId(promoEntity.getItemId());
            if (promoEntity.getStartTime().after(now)) {
                promoModel.setStatus(PromoActiveConstant.NOT_START.getCode());
            } else if (promoEntity.getEndTime().before(now)) {
                promoModel.setStatus(PromoActiveConstant.FINISH.getCode());
            } else {
                promoModel.setStatus(PromoActiveConstant.DO_ING.getCode());
            }
            model.setPromoModel(promoModel);
        }
        //保存数据到redis中,并设置超时时间
        redisTemplate.opsForValue().set("item_data_" + id, model, 120, TimeUnit.SECONDS);
        cacheService.setCommonCache("item_data_" + id, model);
        return model;
    }
}
