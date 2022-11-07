package or.my.springcloud.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MessageListener {
    @RabbitListener(queues = "my-queue")
    public void consumerMessage(String message) {
        log.info("接收到的信息是{}", message);
    }

}
