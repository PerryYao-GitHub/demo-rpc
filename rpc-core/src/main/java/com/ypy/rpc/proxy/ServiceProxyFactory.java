package com.ypy.rpc.proxy;

import com.ypy.rpc.RpcApplication;

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
        if (RpcApplication.getRpcConfig().isMock()) return getMockProxy(serviceClass);

        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }

    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy());
    }
}
