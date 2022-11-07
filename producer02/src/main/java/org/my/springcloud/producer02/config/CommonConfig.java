package org.my.springcloud.producer02.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CommonConfig implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.setReturnsCallback(returnedMessage ->
            log.error("消息发送队列失败，响应码：{},失败原因：{}，交换机：{}，路由key：{}",
                    returnedMessage.getReplyCode(), returnedMessage.getReplyText(),
                    returnedMessage.getExchange(),returnedMessage.getRoutingKey())
        );
    }
}
