package cn.org.qsmx.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq的配置类(用于死信队列的配置）
 */
@Configuration
public class RabbitMQSMSConfig_Dead {

    //定义交换机的名称
    public static final String SMS_EXCHANGE_DEAD = "sms_exchange_dead";

    //定义队列名称
    public static final String SMS_QUEUE_DEAD = "sms_queue_dead";

    //统一定义路由key
    public static final String ROUTING_KEY_SMS_DEAD = "dead.sms.display";

    //创建交换机
    @Bean(SMS_EXCHANGE_DEAD)
    public Exchange DeadExchange(){
        return ExchangeBuilder
                .topicExchange(SMS_EXCHANGE_DEAD)
                .durable(true)
                .build();
    }
    //创建队列
    @Bean(SMS_QUEUE_DEAD)
    public Queue DeadQueue(){
        return QueueBuilder
                .durable(SMS_QUEUE_DEAD)
                .build();
    }

    //创建绑定关系
    @Bean
    public Binding smsDeadBinding(@Qualifier(SMS_EXCHANGE_DEAD) Exchange exchange,
                              @Qualifier(SMS_QUEUE_DEAD) Queue queue){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("dead.sms.#")
                .noargs();
    }
}
