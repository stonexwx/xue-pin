package cn.org.qsmx.mq;

import cn.org.qsmx.pojo.QO.SMSContentQO;
import cn.org.qsmx.retry.RetryComponent;
import cn.org.qsmx.service.ResumeService;
import cn.org.qsmx.util.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
     * @param payload
     * @param message
     */
    @RabbitListener(queues = {InitResumeMQConfig.INIT_RESUME_QUEUE})
    public void watchQueue(String payload, Message message){

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routing = {} "+ routingKey);

        String userId = payload;
        log.info("userId = {}"+ userId);

        if (routingKey.equalsIgnoreCase(InitResumeMQConfig.ROUTING_KEY_INIT_RESUME)){
            resumeService.initResume(userId);
        }
    }
}
