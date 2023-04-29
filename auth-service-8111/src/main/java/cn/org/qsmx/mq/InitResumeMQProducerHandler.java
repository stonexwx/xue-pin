package cn.org.qsmx.mq;

import cn.org.qsmx.pojo.MqLocalMsg;
import cn.org.qsmx.service.MqLocalMsgService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MQ Information handler
 */
@Component
public class InitResumeMQProducerHandler {
    //存储本地消息列表
    private ThreadLocal<List<String>> msgIdsThreadLocal = new ThreadLocal<>();

    @Autowired
    private MqLocalMsgService mqLocalMsgService;

    @Autowired
    public RabbitTemplate rabbitTemplate;

    /**
     * save information to local database
     *
     * @param targetExchange
     * @param routingKey
     * @param msgContent
     */
    public void saveLocalMsg(String targetExchange, String routingKey, String msgContent) {
        MqLocalMsg mqLocalMsg = new MqLocalMsg();
        mqLocalMsg.setTargetExchange(targetExchange);
        mqLocalMsg.setRoutingKey(routingKey);
        mqLocalMsg.setMsgContent(msgContent);
        mqLocalMsg.setCreatedTime(LocalDateTime.now());
        mqLocalMsg.setUpdatedTime(LocalDateTime.now());

        mqLocalMsgService.save(mqLocalMsg);

        //从threadLocal中获取本地消息列表,如果为空则创建一个新的
        List<String> msgIds = msgIdsThreadLocal.get();
        if (CollectionUtils.isEmpty(msgIds)) {
            msgIds = new ArrayList<>();
        }
        //每次消息存储在本地表中后，都需要存储在当前线程中，事务提交以后可以对其进行检查
        msgIds.add(mqLocalMsg.getId());
        msgIdsThreadLocal.set(msgIds);
    }

    /**
     * 发送本地所有的信息给mq
     */
    public void sendAllLocalMsg() {
        List<String> msgIds = msgIdsThreadLocal.get();
        if (CollectionUtils.isEmpty(msgIds)) {
            //如果为空，那么则可能不是当前业务所需要进行的消息发送，可以直接完结
            return;
        }

        //根据msgIds查询所有的本地消息
        List<MqLocalMsg> mqLocalMsgs = mqLocalMsgService.getBatchLocalMsg(msgIds);
        //循环发送消息记录
        for (MqLocalMsg mqLocalMsg : mqLocalMsgs) {
            //发送消息
            rabbitTemplate.convertAndSend(
                    mqLocalMsg.getTargetExchange(),
                    mqLocalMsg.getRoutingKey(),
                    mqLocalMsg.getMsgContent()+","+mqLocalMsg.getId());
        }
    }
}
