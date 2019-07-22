package cn.bucheng.rockmqboot.controller;

import cn.bucheng.rockmqboot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：yinchong
 * @create ：2019/7/22 14:02
 * @description：
 * @modified By：
 * @version:
 */
@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping("createOrder")
    public String createOrder(Long itemId, Long promoId, Long userId, Integer amount, Integer price) {
        orderService.createOrder(itemId, userId, amount, price, promoId);
        return "创建订单成功";
    }
}
