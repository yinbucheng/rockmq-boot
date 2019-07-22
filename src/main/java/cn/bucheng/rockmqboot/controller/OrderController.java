package cn.bucheng.rockmqboot.controller;

import cn.bucheng.rockmqboot.exception.BusinessException;
import cn.bucheng.rockmqboot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

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
    private ExecutorService executor;

    @PostConstruct
    public void init() {
        executor = new ThreadPoolExecutor(20, 200, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200), new ThreadPoolExecutor.AbortPolicy());
    }

    @RequestMapping("createOrder")
    public String createOrder(Long itemId, Long promoId, Long userId, Integer amount, Integer price) {
        orderService.createOrder(itemId, userId, amount, price, promoId);
        return "创建订单成功";
    }


    @RequestMapping("createNewOrder")
    public String createNewOrder(Long itemId, Long promoId, Long userId, Integer amount) {
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                orderService.createNewOrder(itemId, userId, amount, promoId);
                return null;
            }
        });

        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return "创建成功";
    }

}
