package cn.bucheng.rockmqboot.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author ：yinchong
 * @create ：2019/8/1 19:43
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
public class RocketConsumer {

    private DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-topic-consumer");

    private String nameAddress;

    @Before
    public void init() {
        consumer.setNamesrvAddr(nameAddress);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.setConsumeMessageBatchMaxSize(1);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setConsumerGroup("test-group");
        consumer.setConsumeThreadMin(3);
        consumer.setConsumeThreadMax(4);
    }

    @Test
    public void testConsumer() throws MQClientException {
        consumer.subscribe("test-topic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt msg = list.get(0);
                try {

                } catch (Exception e) {
                    log.info("retry consumer message,number:{}", msg.getReconsumeTimes());
                }
                return null;
            }
        });

        consumer.start();
    }

}
