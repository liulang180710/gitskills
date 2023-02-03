package or.my.springcloud.consumer.controller;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.my.springcloud.api.service.HumanInfoRemoteClientManager;
import org.my.springcloud.base.bean.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("human/remote")
public class ConsumerController {
    @Autowired
    private HumanInfoRemoteClientManager humanInfoRemoteClientManager;

    @HystrixCommand(fallbackMethod = "fallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
            },
            threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "2"),
            @HystrixProperty(name = "maxQueueSize", value = "5"),
            @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")}
    )
    @GetMapping(value = "get/{humanID}")
    public ResultInfo getHumanInfo(@PathVariable Integer humanID) {
        return humanInfoRemoteClientManager.getHumanInfo(humanID);
    }

    public ResultInfo fallback(@PathVariable Integer humanID) {
        ResultInfo resultInfo = new ResultInfo(false );
        resultInfo.setMessage("服务降级了！");
        return resultInfo;
    }
}
