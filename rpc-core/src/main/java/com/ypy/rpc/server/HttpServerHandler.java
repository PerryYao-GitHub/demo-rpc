package com.ypy.rpc.server;

import com.ypy.rpc.RpcApplication;
import com.ypy.rpc.registry.LocalRegistry;
import com.ypy.rpc.model.RpcRequest;
import com.ypy.rpc.model.RpcResponse;
import com.ypy.rpc.serializer.Serializer;
import com.ypy.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.lang.reflect.Method;

/**
 * RPC request and response handler *** key codes ***
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
         final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        System.out.println("Received request: " + request.method() + " " + request.uri());
        // asynchronous handle request
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // rpc response
            RpcResponse rpcResponse = new RpcResponse();

            if (rpcRequest == null) {
                // package response
                rpcResponse.setMessage("rpcRequest is null");
                //do response
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                // attain the impl class by reflecting, and trigger the method
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // package response
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // do response
            doResponse(request, rpcResponse, serializer);
        });
    }

    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (Exception e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
