package com.ypy.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ypy.rpc.RpcApplication;
import com.ypy.rpc.serializer.Serializer;
import com.ypy.rpc.model.RpcRequest;
import com.ypy.rpc.model.RpcResponse;
import com.ypy.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest); // serialize the rpc request
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080") // todo
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
