package org.my.springcloud.producer02.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.my.springcloud.base.bean.ResultInfo;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("message/")
public class MessageController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final String routingKey = "my-queue";
    private static final String EXCHANGE = "amq.topic";

    @GetMapping(value = "send")
    public ResultInfo sendMessage(String message) {
        if (StringUtils.isNotEmpty(message)) {
            CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
            correlationData.getFuture().addCallback(result -> {
                if(result.isAck()) {
                    log.info("消息成功发送到交换机,消息ID：{}", correlationData.getId());
                }else {
                    log.info("消息发送到交换机失败,消息ID：{}", correlationData.getId());
                }
                    }
            ,ex -> {
                        log.error("消息发送失败！", ex);
                    });
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, message,correlationData);
        }
        return new ResultInfo(true,"发送消息成功！");
    }
}
