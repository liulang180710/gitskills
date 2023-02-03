package org.my.springcloud.producer.service;
import org.my.springcloud.base.bean.ResultInfo;

import javax.servlet.http.HttpServletRequest;

public interface HumanInfoManager {
    ResultInfo getHumanInfo(Integer humanID);

    ResultInfo findAllHuman();

    ResultInfo login(HttpServletRequest request, String userName, String password);
}
