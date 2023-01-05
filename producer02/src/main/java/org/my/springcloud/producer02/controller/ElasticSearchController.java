package org.my.springcloud.producer02.controller;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.producer02.service.ElasticSearchManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("es/")
public class ElasticSearchController {
    @Autowired
    ElasticSearchManager elasticSearchManager;

    @GetMapping(value = "create")
    public ResultInfo createIndex(String index, String fieldInfo) {
        return elasticSearchManager.createIndex(index, fieldInfo);
    }
}
