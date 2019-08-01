package cn.bucheng.rockmqboot.mq;

import cn.bucheng.rockmqboot.constant.StockLogConstant;
import cn.bucheng.rockmqboot.entity.StockLogEntity;
import cn.bucheng.rockmqboot.mapper.StockLogMapper;
import cn.bucheng.rockmqboot.service.OrderService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：yinchong
 * @create ：2019/7/22 19:19
 * @description：
 * @modified By：
 * @version:
 */
@Component
@Slf4j
public class RockMQProducer {

    private static final int MAX_EXECUTE_NUMBER = 20;

    private TransactionMQProducer transactionMQProducer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.topic.name}")
    private String topicName;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StockLogMapper stockLogMapper;

    @PostConstruct
    public void init() throws MQClientException {
        transactionMQProducer = new TransactionMQProducer("tx_stock_producer");
        transactionMQProducer.setNamesrvAddr(nameAddr);
        transactionMQProducer.start();

        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object arg) {
                log.info("=======execute local transaction======");
                Map<String, Object> argMap = (Map<String, Object>) arg;
                long itemId = (long) argMap.get("itemId");
                int amount = (int) argMap.get("amount");
                long promoId = (long) argMap.get("promoId");
                long userId = (long) argMap.get("userId");
                long stockLogId = (long) argMap.get("stockLogId");
                try {
                    orderService.doCreateNewOrder(itemId, userId, amount, promoId, stockLogId);
                    return LocalTransactionState.COMMIT_MESSAGE;
                } catch (Exception e) {
                    log.error(e.toString());
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                log.info("=========check local transaction============");
                String content = new String(messageExt.getBody());
                Map arg = JSON.parseObject(content, Map.class);
                long stockLogId = (long) arg.get("stockLogId");
                StockLogEntity stockLogEntity = stockLogMapper.selectById(stockLogId);
                if (stockLogEntity == null) {
                    return LocalTransactionState.UNKNOW;
                }
                if (stockLogEntity.getStatus() == StockLogConstant.WAIT_EXECUTE) {
                    if (stockLogEntity.getExecuteNum() > MAX_EXECUTE_NUMBER) {
                        return LocalTransactionState.ROLLBACK_MESSAGE;
                    }
                    int executeNum = stockLogEntity.getExecuteNum() + 1;
                    stockLogEntity.setExecuteNum(executeNum);
                    Integer rows = stockLogMapper.updateById(stockLogEntity);
                    if (rows <= 0) {
                        log.error("update stockLog fail,stockLogID:{}", stockLogId);
                    }
                    return LocalTransactionState.UNKNOW;
                } else if (stockLogEntity.getStatus() == StockLogConstant.COMMIT) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (stockLogEntity.getStatus() == StockLogConstant.ROLLBACK) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.UNKNOW;
            }
        });
    }


    public boolean txAsyncReduceStock(long itemId, long userId, long promoId, int amount, long stockLogId) {
        Map<String, Object> param = new HashMap<>();
        param.put("itemId", itemId);
        param.put("amount", amount);
        param.put("promoId", promoId);
        param.put("userId", userId);
        param.put("stockLogId", stockLogId);

        Message message = new Message(topicName, "increase",
                JSON.toJSON(param).toString().getBytes(Charset.forName("UTF-8")));

        try {
            TransactionSendResult txResult = transactionMQProducer.sendMessageInTransaction(message, param);
            LocalTransactionState localTransactionState = txResult.getLocalTransactionState();
            if (localTransactionState == LocalTransactionState.COMMIT_MESSAGE) {
                return true;
            } else if (localTransactionState == LocalTransactionState.ROLLBACK_MESSAGE) {
                return false;
            }
            return false;
        } catch (MQClientException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }

}
