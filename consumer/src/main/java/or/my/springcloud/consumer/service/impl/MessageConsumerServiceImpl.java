package or.my.springcloud.consumer.service.impl;

import or.my.springcloud.consumer.service.MessageConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class MessageConsumerServiceImpl implements MessageConsumerService {
    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(MessageConsumerServiceImpl.class);
    @Bean
    public Consumer<String> input() {
        return message -> logger.info("接收消息为：{}", message);
    }

}
