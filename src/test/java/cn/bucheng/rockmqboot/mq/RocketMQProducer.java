package cn.bucheng.rockmqboot.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/8/1 19:43
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
public class RocketMQProducer {

    private TransactionMQProducer producer = new TransactionMQProducer("test-tx-producer");
    private String nameAddress = "";

    @Before
    public void init() throws MQClientException {
        producer.setExecutorService(new ThreadPoolExecutor(2, 5, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(20), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("test-tx-producer");
                return thread;
            }
        }));

        producer.setNamesrvAddr(nameAddress);
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                log.info("execute local tx ,arg:{}", o);
                return LocalTransactionState.UNKNOW;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                log.info("execute local check tx");
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        producer.start();
    }

    @Test
    public void testSendMessage() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        Message message = new Message("test-topic", "test hello word".getBytes());
        producer.sendMessageInTransaction(message, "test hello word");
    }
}
