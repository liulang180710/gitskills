package org.my.springcloud.producer02.service;



import org.my.springcloud.base.bean.HumanInfo;
import org.my.springcloud.base.bean.ResultInfo;

import java.util.List;

public interface HumanInfoManager {
    ResultInfo getHumanInfo(Integer humanID);

    ResultInfo findAllHuman();
}
