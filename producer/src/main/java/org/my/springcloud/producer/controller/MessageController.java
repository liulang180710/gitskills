package org.my.springcloud.producer.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.producer.service.HumanInfoManager;
import org.my.springcloud.producer.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("message/")
//@RefreshScope
public class MessageController {
    @Autowired
    private ProducerService producerService;

    //@Value("${config.info}")
    private String info;

    @GetMapping(value = "send")
    public ResultInfo sendMessage(String message) {
        return producerService.sendTimeMsg(message);
    }

    @GetMapping(value = "send/without/info")
    public ResultInfo sendMessageWithOutInfo() {
        return producerService.sendTimeMsg(info);
    }
}
