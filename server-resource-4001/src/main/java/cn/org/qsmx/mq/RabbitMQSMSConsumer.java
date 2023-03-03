package cn.org.qsmx.mq;

import cn.org.qsmx.pojo.QO.SMSContentQO;
import cn.org.qsmx.retry.RetryComponent;
import cn.org.qsmx.util.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 短信监听消费者
 */
@Slf4j
@Component
public class RabbitMQSMSConsumer {

    @Autowired
    private RetryComponent retryComponent;
    /**
     * 监听队列，并处理消息
     * @param payload
     * @param message
     */
    @RabbitListener(queues = {RabbitMQSMSConfig.SMS_QUEUE})
    public void watchQueue(String payload, Message message){

        log.info("payload = "+payload);

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routing = "+ routingKey);

        String msg = payload;
        log.info("msg = "+ msg);

        if (routingKey.equalsIgnoreCase(RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN)){
            //此处为短信发送的消息消费处理
            SMSContentQO smsContentQO = GsonUtils.stringToBean(msg, SMSContentQO.class);
            retryComponent.sendSmsWithRetry(smsContentQO.getMobile(),smsContentQO.getContent());
        }
    }
}
