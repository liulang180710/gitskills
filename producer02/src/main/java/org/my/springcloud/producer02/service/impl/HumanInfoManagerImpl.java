package org.my.springcloud.producer02.service.impl;

import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.producer02.dao.HumanInfoMapper;
import org.my.springcloud.producer02.service.HumanInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HumanInfoManagerImpl implements HumanInfoManager {
    @Autowired
    HumanInfoMapper humanInfoMapper;


    @Override
    public ResultInfo getHumanInfo(Integer humanID) {
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e) {

        }
        ResultInfo resultInfo = new ResultInfo(true);
        resultInfo.setData("data", humanInfoMapper.getHumanInfo(humanID));
        return resultInfo;
    }

    @Override
    public ResultInfo findAllHuman() {
        ResultInfo resultInfo = new ResultInfo(true);
        resultInfo.setData("data", humanInfoMapper.findAllHuman());
        return resultInfo;
    }
}
