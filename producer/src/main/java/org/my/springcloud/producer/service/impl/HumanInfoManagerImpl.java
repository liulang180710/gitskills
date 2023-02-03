package org.my.springcloud.producer.service.impl;

import com.alibaba.druid.util.StringUtils;
import org.my.springcloud.base.bean.HumanInfo;
import org.my.springcloud.base.bean.HumanSession;
import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.producer.dao.HumanInfoMapper;
import org.my.springcloud.producer.service.HumanInfoManager;
import org.my.springcloud.producer.utils.SessionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class HumanInfoManagerImpl implements HumanInfoManager {
    @Autowired
    HumanInfoMapper humanInfoMapper;


    @Override
    public ResultInfo getHumanInfo(Integer humanID) {
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

    @Override
    public ResultInfo login(HttpServletRequest request, String userName, String password) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return new ResultInfo(false, "用户名或密码为空！");
        }
        //验证用户名和密码
        HumanInfo humanInfo = humanInfoMapper.validPassword(userName, password);
        if (humanInfo == null) {
            return new ResultInfo(false, "用户名或者密码错误！");
        }
        HumanSession humanSession = new HumanSession();
        BeanUtils.copyProperties(humanInfo, humanSession);
        SessionUtils.logon(request, humanSession);
        //如果验证成功
        return new ResultInfo(true, "登录成功！");
    }

}
