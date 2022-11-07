package org.my.springcloud.producer.service;
import org.my.springcloud.base.bean.ResultInfo;

public interface HumanInfoManager {
    ResultInfo getHumanInfo(Integer humanID);

    ResultInfo findAllHuman();
}
