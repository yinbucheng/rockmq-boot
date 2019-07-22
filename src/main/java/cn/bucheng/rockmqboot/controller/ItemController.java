package cn.bucheng.rockmqboot.controller;

import cn.bucheng.rockmqboot.entity.ItemEntity;
import cn.bucheng.rockmqboot.service.ItemService;
import cn.bucheng.rockmqboot.vo.ItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：yinchong
 * @create ：2019/7/22 14:02
 * @description：
 * @modified By：
 * @version:
 */
@RestController
@RequestMapping("/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @RequestMapping("listAll")
    public List<ItemEntity> listAll() {
        return itemService.listItems();
    }

    @RequestMapping("getItem")
    public ItemModel getItem(Long id) {
        return itemService.findItem(id);
    }
}
