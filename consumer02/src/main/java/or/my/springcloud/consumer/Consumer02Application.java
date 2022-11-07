package or.my.springcloud.consumer;

import org.my.springcloud.api.service.HumanInfoRemoteClientManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {HumanInfoRemoteClientManager.class})
@EnableHystrix
public class Consumer02Application {
    public static void main(String[] args) {
        SpringApplication.run(Consumer02Application.class, args);
    }

}
