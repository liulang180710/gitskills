package org.my.springcloud.producer.controller;

import org.my.springcloud.base.bean.HumanInfo;
import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.base.currentlimiter.GuavaLimit;
import org.my.springcloud.base.currentlimiter.RedisLimit;
import org.my.springcloud.producer.service.HumanInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("human/")
public class HumanInfoController {
    @Autowired
    private HumanInfoManager humanInfoManager;

    @GuavaLimit(key = "getHumanInfo", permitsPerSecond = 1, timeout = 500, timeunit = TimeUnit.MILLISECONDS, msg = "当前查询人数较多，请稍后再试！")
    @GetMapping(value = "get/{humanID}")
    public ResultInfo getHumanInfo(@PathVariable Integer humanID) {
        System.out.println("***************8004******************");
        return humanInfoManager.getHumanInfo(humanID);
    }

    @RedisLimit(key = "redis-limit:loginHandler", permitsPerSecond = 2, expire = 1, msg = "当前登录人数较多，请稍后再试！")
    @PostMapping(value = "login")
    public ResultInfo loginHandler(HttpServletRequest request,
            @RequestParam(required = false) String userName,
                                   @RequestParam(required = false) String password) {
        return humanInfoManager.login(request,userName,password);
    }

    @GetMapping(value = "getAll")
        public ResultInfo getAllHumanInfo() {
            return humanInfoManager.findAllHuman();
    }


    @PostMapping(value = "create/human")
    public ResultInfo createHuman(HumanInfo humanInfo) {
        return humanInfoManager.createHuman(humanInfo);
    }


}
