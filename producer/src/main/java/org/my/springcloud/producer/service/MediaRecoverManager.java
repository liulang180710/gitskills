package org.my.springcloud.producer.service;

import org.my.springcloud.base.bean.ResultInfo;

public interface MediaRecoverManager {
    ResultInfo reloadMediaInfoByFilePath(String filePath, String mediaUsage);
}
