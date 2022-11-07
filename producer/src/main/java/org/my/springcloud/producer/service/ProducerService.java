package org.my.springcloud.producer.service;

import org.my.springcloud.base.bean.ResultInfo;

public interface ProducerService {

    ResultInfo sendTimeMsg(String message);

}
