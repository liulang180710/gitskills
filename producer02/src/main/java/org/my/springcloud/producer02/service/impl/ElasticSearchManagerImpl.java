package org.my.springcloud.producer02.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.my.springcloud.base.bean.HumanInfo;
import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.producer02.dao.HumanInfoMapper;
import org.my.springcloud.producer02.service.ElasticSearchManager;
import org.my.springcloud.producer02.service.HumanInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ElasticSearchManagerImpl implements ElasticSearchManager {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private HumanInfoManager humanInfoManager;

    @Override
    public ResultInfo createIndex(String indexName, String fieldInfo) {
        if (StringUtils.isEmpty(indexName)) {
            return new ResultInfo(false, "索引库名称为null");
        }
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.source(fieldInfo, XContentType.JSON);
        try {
            restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }catch (IOException e) {
            log.error("创建索引库失败！失败原因{}", e.getMessage());
        }
        return new ResultInfo(true, "创建索引库成功！");
    }

    @Override
    public ResultInfo indexExist(String indexName) {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        boolean isExist = false;
        try{
            isExist = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        }catch (IOException e) {
            log.error("查询索引库失败！失败原因{}", e.getMessage());
        }
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setSuccess(isExist);
        return resultInfo;
    }

    @Override
    public ResultInfo deleteIndex(String indexName) {
        ResultInfo resultInfo = new ResultInfo(true);
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
        try{
            restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        }catch (IOException e) {
            log.error("删除索引库失败！失败原因{}", e.getMessage());
            resultInfo.setSuccess(false);
            resultInfo.setMessage("删除索引库失败！");
        }
        return resultInfo;
    }

    @Override
    public ResultInfo addInfo(Integer humanID) {
        ResultInfo resultInfo = humanInfoManager.getHumanInfo(humanID);
        if (resultInfo.isSuccess()) {
            HumanInfo humanInfo = (HumanInfo) resultInfo.getData("data");
            JSONObject json = new JSONObject();
            json.put("name", humanInfo.getHumanName());
            json.put("age", humanInfo.getAge());
            json.put("telMobile", humanInfo.getTelMobile());
            IndexRequest request = new IndexRequest("human").id(humanID.toString());
            request.source(json.toJSONString(), XContentType.JSON);
            try{
                restHighLevelClient.index(request, RequestOptions.DEFAULT);
            }catch (IOException e) {
                log.error("新增文档失败！失败原因{}", e.getMessage());
                resultInfo.setSuccess(false);
                resultInfo.setMessage("新增文档失败！");
            }
        }
        return resultInfo;
    }

    @Override
    public HumanInfo getHumanInfo(Integer humanID) {
        GetRequest getRequest = new GetRequest("human", humanID.toString());
        try{
           GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
           String json = getResponse.getSourceAsString();
           return JSONObject.parseObject(json, HumanInfo.class);
        }catch (IOException e) {
            log.error("查询文档失败！失败原因{}", e.getMessage());
        }
        return null;
    }

    @Override
    public ResultInfo updateHumanInfo(HumanInfo humanInfo) {
        UpdateRequest updateRequest = new UpdateRequest("human", humanInfo.getHumanID() + "");
        try{
            updateRequest.doc(JSONObject.toJSONString(humanInfo));
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        }catch (IOException e) {
            log.error("更新文档失败！失败原因{}", e.getMessage());
            return new ResultInfo(false, "更新文档失败");
        }
        return new ResultInfo(true);
    }

    @Override
    public ResultInfo batchAddInfo() {
        ResultInfo resultInfo = humanInfoManager.findAllHuman();
        if (resultInfo.isSuccess()) {
            List<HumanInfo> humanInfoList = (List<HumanInfo>) resultInfo.getData("data");
            if (!CollectionUtils.isEmpty(humanInfoList)) {
                BulkRequest bulkRequest = new BulkRequest();
                humanInfoList.stream().map(humanInfo -> {
                    bulkRequest.add(new IndexRequest("human")
                            .id(humanInfo.getHumanID()+ "")
                            .source(JSONObject.toJSONString(humanInfo),XContentType.JSON));
                    return bulkRequest;
                });
                try{
                    restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                }catch (IOException e) {
                    log.error("批量插入文档失败！失败原因{}", e.getMessage());
                    return new ResultInfo(false, "批量插入文档失败");
                }
            }
        }
        return resultInfo;
    }
}
