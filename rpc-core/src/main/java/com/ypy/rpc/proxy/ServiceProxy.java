package com.ypy.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ypy.rpc.RpcApplication;
import com.ypy.rpc.RpcConstant;
import com.ypy.rpc.config.RpcConfig;
import com.ypy.rpc.model.ServiceMetaInfo;
import com.ypy.rpc.registry.Registry;
import com.ypy.rpc.registry.RegistryFactory;
import com.ypy.rpc.serializer.Serializer;
import com.ypy.rpc.model.RpcRequest;
import com.ypy.rpc.model.RpcResponse;
import com.ypy.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * JDK dynamic proxy
 */
public class ServiceProxy implements InvocationHandler {

    /**
     *
     * @param proxy the proxy instance that the method was invoked on
     *
     * @param method the {@code Method} instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the {@code Method} object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or {@code null} if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * {@code java.lang.Integer} or {@code java.lang.Boolean}.
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        // build request
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest); // serialize the rpc request
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // String postUrl = "http://" + rpcConfig.getServerHost() + ":" + rpcConfig.getServerPort();
            // get service addrees from register
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (serviceMetaInfos.isEmpty()) throw new RuntimeException("no service url");
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfos.get(0); // choose the first service temporarily

            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddr())
                    .body(bodyBytes)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class); // deserialize the rpc response
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
