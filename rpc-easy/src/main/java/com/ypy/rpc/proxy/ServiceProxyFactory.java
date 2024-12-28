package com.ypy.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * proxy factory, create proxy object
 */
public class ServiceProxyFactory {
    /**
     * provide consumer with proxy object
     *
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
