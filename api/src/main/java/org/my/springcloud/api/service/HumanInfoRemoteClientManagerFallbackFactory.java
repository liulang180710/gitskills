package org.my.springcloud.api.service;

import org.my.springcloud.base.bean.ResultInfo;
import org.springframework.cloud.openfeign.FallbackFactory;

public class HumanInfoRemoteClientManagerFallbackFactory implements FallbackFactory<HumanInfoRemoteClientManager> {
    @Override
    public HumanInfoRemoteClientManager create(Throwable cause) {
        return new HumanInfoRemoteClientManager() {
            @Override
            public ResultInfo getHumanInfo(Integer humanID) {
                return new ResultInfo(false, "请稍后再试！");
            }

            @Override
            public ResultInfo getAllHumanInfo() {
                return new ResultInfo(false, "请稍后再试！");
            }
        };
    }
}
