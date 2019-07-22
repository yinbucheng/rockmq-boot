package cn.bucheng.rockmqboot.mapper;

import cn.bucheng.rockmqboot.entity.ItemEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ：yinchong
 * @create ：2019/7/22 13:57
 * @description：
 * @modified By：
 * @version:
 */
public interface ItemMapper extends BaseMapper<ItemEntity> {
    int incrementSales(@RequestParam("itemId") long itemId,@RequestParam("amount") int amount);
}
