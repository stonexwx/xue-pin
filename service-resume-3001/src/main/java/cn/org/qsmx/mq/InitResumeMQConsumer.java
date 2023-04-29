package cn.org.qsmx.mq;

import cn.org.qsmx.service.ResumeService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 创建简历监听消费者
 */
@Slf4j
@Component
public class InitResumeMQConsumer {

    @Autowired
    private ResumeService resumeService;
    /**
     * 监听队列，并处理消息
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {InitResumeMQConfig.INIT_RESUME_QUEUE})
    public void watchQueue(Message message, Channel channel) throws IOException {

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routing = {} "+ routingKey);

        String msg = new String(message.getBody());

        String userId = msg.split(",")[0];
        String msgId = msg.split(",")[1];
        log.info("userId = {}"+ userId);

        try {
            if (routingKey.equalsIgnoreCase(InitResumeMQConfig.ROUTING_KEY_INIT_RESUME)){
                resumeService.initResume(userId,msgId);

                //运行成功，ack确认
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
                log.info("ack success,id:{}",msgId);
            }
        }catch (Exception e){
            //运行失败，重回队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),true,true);
            log.info("ack error,id:{}",msgId);
        }

    }
}
