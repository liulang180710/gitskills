package org.my.springcloud.producer.service.impl;

import org.my.springcloud.producer.service.HumanInfoManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HumanInfoManagerProxy {
    public static HumanInfoManager getProxy(final HumanInfoManager humanInfoManager){
        ClassLoader loader = humanInfoManager.getClass().getClassLoader();
        Class<?>[] interfaces = humanInfoManager.getClass().getInterfaces();
        InvocationHandler h = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                try {
                    result = method.invoke(humanInfoManager, args);
                } catch (Exception e) {
                } finally {
                }
                return result;
            }
        };
        Object proxy = Proxy.newProxyInstance(loader, interfaces, h);
        return (HumanInfoManager) proxy;
    }
}
