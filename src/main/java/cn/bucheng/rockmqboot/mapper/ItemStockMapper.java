package cn.bucheng.rockmqboot.mapper;

import cn.bucheng.rockmqboot.entity.ItemStockEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.web.bind.annotation.RequestParam;

public interface ItemStockMapper extends BaseMapper<ItemStockEntity> {
    int decrementStock(@RequestParam("itemStockId") long itemStockId, @RequestParam("amount") int amount);
}
