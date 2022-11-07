package org.my.springcloud.producer02.controller;
import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.producer02.service.HumanInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("human/")
public class HumanInfoController {
    @Autowired
    private HumanInfoManager humanInfoManager;


    @GetMapping(value = "get/{humanID}")
    public ResultInfo getHumanInfo(@PathVariable Integer humanID) {
        System.out.println("***************8005******************");
        return humanInfoManager.getHumanInfo(humanID);
    }

    @GetMapping(value = "getAll")
        public ResultInfo getAllHumanInfo() {
            return humanInfoManager.findAllHuman();
    }
}
