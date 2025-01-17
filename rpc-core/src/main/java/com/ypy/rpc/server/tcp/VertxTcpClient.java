package com.ypy.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.ypy.rpc.RpcApplication;
import com.ypy.rpc.model.RpcRequest;
import com.ypy.rpc.model.RpcResponse;
import com.ypy.rpc.model.ServiceMetaInfo;
import com.ypy.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VertxTcpClient {
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        // send tcp request
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                res -> {
                    if (!res.succeeded()) {
                        System.err.println("TCP connect failed");
                        return;
                    }

                    System.out.println("TCP connect successfully");
                    NetSocket socket = res.result();
                    // make data
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer(ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType(ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // send data
                    try {
                        Buffer buffer = ProtocolMessageUtils.encode(protocolMessage);
                        socket.write(buffer);
                    } catch (IOException e) {
                        throw new RuntimeException("protocol encode failed");
                    }

                    // get response data, use Wrapper
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageUtils.decode(buffer);
                                    responseFuture.complete(responseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("protocol decode failed");
                                }
                            });

                    socket.handler(bufferHandlerWrapper);
                }); // block here, when response finished, then go on

        RpcResponse rpcResponse = responseFuture.get();
        // close connect
        netClient.close();
        return rpcResponse;
    }
}
