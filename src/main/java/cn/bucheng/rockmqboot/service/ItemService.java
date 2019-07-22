package cn.bucheng.rockmqboot.service;

import cn.bucheng.rockmqboot.entity.ItemEntity;
import cn.bucheng.rockmqboot.vo.ItemModel;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;


public interface ItemService extends IService<ItemEntity> {
    List<ItemEntity> listItems();

    ItemModel findItem(Long id);
}
