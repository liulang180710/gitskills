package org.my.springcloud.producer.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.producer.service.MediaRecoverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MediaRecoverManagerImpl implements MediaRecoverManager {
    private final static Logger logger = LoggerFactory.getLogger(MediaRecoverManagerImpl.class);

    @Override
    public ResultInfo reloadMediaInfoByFilePath(String filePath, String mediaUsage) {
        if (StringUtils.isEmpty(filePath)) {
            return new ResultInfo(false, "请输入文件地址！");
        }
        if (StringUtils.isEmpty(mediaUsage)) {
            return new ResultInfo(false, "请输入mediaUsage类型！");
        }
        ExecutorService executorService = new ThreadPoolExecutor(10,50,5L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(5000),new ThreadFactoryBuilder().setNameFormat("MediaRecoverManager-%d").build(),new ThreadPoolExecutor.CallerRunsPolicy());

        ResultInfo resultInfo = new ResultInfo(true);
        ArrayList<String> filePathList = new ArrayList<>();
        AtomicInteger successesNum = new AtomicInteger(0);
        getFilePath(filePath, filePathList);
        int totalNum = filePathList.size();
        CountDownLatch countDownLatch = new CountDownLatch(totalNum);
        AtomicLong number = new AtomicLong(0);
        filePathList.forEach((mediaPath) -> executorService.execute(() -> {
            if (insertMediaInfo(mediaPath, number, mediaUsage).isSuccess()) {
                successesNum.getAndIncrement();
            }
            countDownLatch.countDown();
        }));
        try {
            countDownLatch.await();
            resultInfo.setData("totalNum", totalNum);
            resultInfo.setData("successesNum", successesNum);
        } catch (InterruptedException e) {
            logger.error("线程中断异常", e);
        }finally {
            executorService.shutdown();
        }
        return resultInfo;

    }

    //C:/v11_rec/WFAttach/403/42/Event4034251/2020002.jpg
    //循环遍历文件夹
    public void getFilePath(String filePath, ArrayList<String> filePathList ) {
        File file = new File(filePath);
        // 测试此抽象路径名表示的文件是否为目录。
        if(file.isDirectory()) {
            // 返回一个抽象路径名数组，表示由该抽象路径名表示的目录中的文件。
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                getFilePath(files[i].getAbsolutePath(), filePathList);
            }
        }else {
            filePathList.add(filePath);
        }
    }

    private ResultInfo insertMediaInfo(String mediaPath, AtomicLong number, String mediaUsage) {
        //获取多媒体名称
        ResultInfo resultInfo = new ResultInfo(false);
        String[] tempArray = mediaPath.split("/|\\\\");
        if (tempArray.length >= 2) {
            String mediaName = tempArray[tempArray.length-1];
            String recIdStr = tempArray[tempArray.length-2];
            if (StringUtils.isNotEmpty(recIdStr) && recIdStr.contains("Event")) {
                long recId = -1 * Integer.parseInt(recIdStr.substring(5));
                if (!isInTable(recId, mediaName))  {
                    //TODO
                }else {
                    logger.error("数据已经被插入！");
                }
            }
        }else {
            logger.error("数据格式异常！");
        }
        return resultInfo;
    }
    private boolean isInTable(long recId, String mediaName) {
        //TODO
        return false;
    }
}
