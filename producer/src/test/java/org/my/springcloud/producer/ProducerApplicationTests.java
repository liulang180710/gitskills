package org.my.springcloud.producer;

import org.junit.jupiter.api.Test;
import org.my.springcloud.producer.service.HumanInfoManager;
import org.my.springcloud.producer.service.MyService;
import org.my.springcloud.producer.service.impl.HumanInfoManagerImpl;
import org.my.springcloud.producer.service.impl.HumanInfoManagerProxy;
import org.my.springcloud.producer.service.impl.MyCglib;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;

@SpringBootTest
class ProducerApplicationTests {

    @Test
    void test01() {
        /** 动态代理创建的字节码文件存储到本地 */
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "cglib_proxy");
        /** 通过cg11b动态代理获取代理对象的过程，创建调用的对象 */
        Enhancer enhancer = new Enhancer();
        /** 设置enhancer对象的父类 */
        enhancer.setSuperclass(MyService.class);
        /** 设置enhancer的回调对象 */
        enhancer.setCallback(new MyCglib());
        /** 创建代理对象 */
        MyService myService = (MyService) enhancer.create();
        /** 通过代理对象调用目标方法 */
        System.out.println(myService.B(999));
        System.out.println(myService.getClass());
    }


    @Test
    void test02() {
        HumanInfoManager proxy = HumanInfoManagerProxy.getProxy(new HumanInfoManagerImpl());
        System.out.println(proxy.getHumanInfo(111));
    }

}
