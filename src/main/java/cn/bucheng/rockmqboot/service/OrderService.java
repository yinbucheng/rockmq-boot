package cn.bucheng.rockmqboot.service;

import cn.bucheng.rockmqboot.entity.OrderEntity;
import com.baomidou.mybatisplus.service.IService;

/**
 * @author ：yinchong
 * @create ：2019/7/22 14:34
 * @description：
 * @modified By：
 * @version:
 */
public interface OrderService  extends IService<OrderEntity> {
    void createOrder(Long itemId,Long userId,Integer amount,Integer price,Long promoId);

    void createNewOrder(Long itemId,Long userId,Integer amount,Long promoId);

    void doCreateNewOrder(Long itemId,Long userId,Integer amount,Long promoId,Long stockLogId);
}
