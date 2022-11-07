package org.my.springcloud.producer.service.impl;

import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.producer.service.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ProducerServiceImpl implements ProducerService {
    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(ProducerServiceImpl.class);
    @Autowired
    private StreamBridge streamBridge;

    private String format = "yyyy-mm-dd  HH:mm:ss";

    @Override
    public ResultInfo sendTimeMsg(String message) {
        logger.info(new SimpleDateFormat(format).format(new Date()));
        boolean flag = streamBridge.send("output-out-0",
                MessageBuilder.withPayload(message).build());
        ResultInfo resultInfo = new ResultInfo(flag);
        resultInfo.setMessage(flag ? "发送消息成功！" : "发送消息失败！");
        return resultInfo;
    }
}
