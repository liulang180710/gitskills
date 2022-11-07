package org.my.springcloud.api.service;

import org.my.springcloud.base.bean.ResultInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "my-producer-server", fallbackFactory = HumanInfoRemoteClientManagerFallbackFactory.class )
public interface HumanInfoRemoteClientManager {

    @GetMapping(value = "human/get/{humanID}")
    ResultInfo getHumanInfo(@PathVariable Integer humanID);

    @GetMapping(value = "getAll")
    ResultInfo getAllHumanInfo();
}
