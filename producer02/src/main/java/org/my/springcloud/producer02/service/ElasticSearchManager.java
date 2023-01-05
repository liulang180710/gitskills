package org.my.springcloud.producer02.service;



import org.my.springcloud.base.bean.HumanInfo;
import org.my.springcloud.base.bean.ResultInfo;

public interface ElasticSearchManager {

    ResultInfo createIndex(String indexName, String fieldInfo);

    ResultInfo indexExist(String indexName);

    ResultInfo deleteIndex(String indexName);

    ResultInfo addInfo(Integer humanID);

    HumanInfo getHumanInfo(Integer humanID);

    ResultInfo updateHumanInfo(HumanInfo humanInfo);

    ResultInfo batchAddInfo();

}
