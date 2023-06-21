package org.my.springcloud.producer.utils;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Demo {
    class Data {
        private String key;
        private Object object;

        public Data(String key, Object object) {
            this.key = key;
            this.object = object;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }


    private List<Data> dataList =  new ArrayList<>();
    // 初始表
    private ArrayList<List<Data>> table = new ArrayList(16);

    private volatile int size = 0 ;

    // 阈值系数
    private float maxSizeRate = 0.75f;

    // 创建初始表
    private void createList(int size) {
        for (int i = 0 ; i< size ;i ++) {
            List<Data> list =  new ArrayList<>();
            table.add(list);
        }
    }





    private Object put(String key, Object object) {
        int index = key.hashCode() % table.size();
        Object finalObject = null;
        if (table.get(index) != null) {
            List<Data> dataList = table.get(index);
            if (dataList != null) {
                boolean isExist = dataList.stream().filter(data -> data.key.equals(key) && data.object == object).findAny().isPresent();
                if (isExist) {
                    //返回旧的Object
                    for (int i= 0 ; i < dataList.size() ;i++) {
                        if (dataList.get(0).key.equals(key)) {
                            finalObject = object;
                            break;
                        }
                    }
                }else {
                    // 加入到链表中，如果大于8要转化成红黑树
                    if (dataList.size()+ 1 <= 8) {
                        dataList.add(new Data(key, object));
                    }else {

                    }
                }

            }else {
                // 需要根据阈值判断是否扩容

                if (table.size() * maxSizeRate > size ++) {
                    //扩容
                }else {
                    List<Data> arrayList = new ArrayList();
                    arrayList.add(new Data(key, object));
                }

            }
        }
        return finalObject;
    }

    private Object get(String key) {
        int index = key.hashCode();
        Object[] objectArray = null;
        List<Data> dataList = table.get(index);
        if (!CollectionUtils.isEmpty(dataList)) {
            dataList.forEach(data -> {
                if( data.key.equals(key)) {
                    objectArray[0] = data.object;
                    return;
                }
            });
        }else {
            return null;
        }
        return objectArray[0];
    }

    public static void main(String[] args) {
        Demo demoMap = new Demo();
        demoMap.createList(10);
        demoMap.put("1", "ceshi");
        System.out.println(demoMap.get("1").toString());
    }


}
