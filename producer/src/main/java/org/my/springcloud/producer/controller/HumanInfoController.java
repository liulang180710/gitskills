package org.my.springcloud.producer.controller;

import org.my.springcloud.base.bean.HumanInfo;
import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.producer.service.HumanInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("human/")
public class HumanInfoController {
    @Autowired
    private HumanInfoManager humanInfoManager;


    @GetMapping(value = "get/{humanID}")
    public ResultInfo getHumanInfo(@PathVariable Integer humanID) {
        System.out.println("***************8004******************");
        return humanInfoManager.getHumanInfo(humanID);
    }

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


}
