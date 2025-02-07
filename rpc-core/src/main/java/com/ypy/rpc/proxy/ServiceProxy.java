package com.ypy.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ypy.rpc.RpcApplication;
import com.ypy.rpc.RpcConstant;
import com.ypy.rpc.config.RpcConfig;
import com.ypy.rpc.fault.retry.RetryStrategy;
import com.ypy.rpc.fault.retry.RetryStrategyFactory;
import com.ypy.rpc.fault.tolerant.TolerantStrategy;
import com.ypy.rpc.fault.tolerant.TolerantStrategyFactory;
import com.ypy.rpc.loadbalancer.LoadBalancer;
import com.ypy.rpc.loadbalancer.LoadBalancerFactory;
import com.ypy.rpc.model.ServiceMetaInfo;
import com.ypy.rpc.protocol.*;
import com.ypy.rpc.registry.Registry;
import com.ypy.rpc.registry.RegistryFactory;
import com.ypy.rpc.serializer.Serializer;
import com.ypy.rpc.model.RpcRequest;
import com.ypy.rpc.model.RpcResponse;
import com.ypy.rpc.serializer.SerializerFactory;
import com.ypy.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * JDK dynamic proxy
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // build request
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (serviceMetaInfos.isEmpty()) throw new RuntimeException("no service url");
//        ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfos.get(0); // choose the first service temporarily
//      use loadbalance
        LoadBalancer lb = LoadBalancerFactory.getInstance(rpcConfig.getLoadbalancer());
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = lb.select(requestParams, serviceMetaInfos);

//        RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo); // "get response" move into VertxTcpClient !!!
//      use retry strategy and tolerant strategy
        RpcResponse rpcResponse;
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(()->VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo));
        } catch (Exception e) {
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }
//        rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
        return rpcResponse.getData();

        /* http request
        try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddr())
                .body(bodyBytes)
                .execute()) {
            byte[] result = httpResponse.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class); // deserialize the rpc response
            return rpcResponse.getData();
        }
        */
    }
}
