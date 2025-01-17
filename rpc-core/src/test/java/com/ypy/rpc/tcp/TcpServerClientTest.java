package com.ypy.rpc.tcp;

import com.ypy.rpc.model.RpcRequest;
import com.ypy.rpc.protocol.*;
import com.ypy.rpc.server.tcp.TcpServerHandler;
import com.ypy.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

import java.io.IOException;

public class TcpServerClientTest {

    private static final int PORT = 8888;

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();

        // 启动服务器
        startServer(vertx);

        // 启动客户端并模拟请求
        startClient(vertx);

        // 等待一段时间以便客户端和服务器之间进行交互
        Thread.sleep(5000);

        // 可添加更多调试或验证逻辑，检查客户端与服务器之间的交互
        vertx.close();
    }

    private static void startServer(Vertx vertx) {
        NetServer server = vertx.createNetServer();

//        server.connectHandler(new TcpServerHandler());
        // 处理每个连接
        server.connectHandler(socket -> {
            // 输出连接信息
            System.out.println("Client connected: " + socket.remoteAddress());

            // 读取接收到的数据
            socket.handler(buffer -> {
                // 打印接收到的数据
                System.out.println("----");
                System.out.println("Received data: " + buffer);
                System.out.println(buffer.getByte(0)); // magic
                System.out.println(buffer.getByte(1)); // version
                System.out.println(buffer.getByte(2)); // serializer
                System.out.println(buffer.getByte(3)); // type
                System.out.println(buffer.getByte(4)); // status
                System.out.println(buffer.getLong(5)); // id
                System.out.println(buffer.getInt(13)); // bodyLength
                System.out.println(buffer.getBytes(17, 17 + buffer.getInt(13)).toString());
                System.out.println("----");
            });

            // 处理连接关闭
            socket.closeHandler(v -> {
                System.out.println("Client disconnected: " + socket.remoteAddress());
            });
        });

        server.listen(PORT, res -> {
            if (res.succeeded()) {
                System.out.println("TCP Server started on port " + PORT);
            } else {
                System.err.println("TCP Server failed to start");
            }
        });
    }

    private static void startClient(Vertx vertx) {
        NetClient client = vertx.createNetClient();
        client.connect(PORT, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected to server");

                NetSocket socket = res.result();

                // 创建并发送一个模拟的 RpcRequest
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setServiceName("TestService");
                rpcRequest.setMethodName("testMethod");
                rpcRequest.setParameterTypes(new Class<?>[]{String.class});
                rpcRequest.setArgs(new Object[]{"Test argument"});

                // 创建 ProtocolMessage 包装 RpcRequest
                ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                header.setSerializer(ProtocolMessageSerializerEnum.getEnumByValue("jdk").getKey());
                header.setType(ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setStatus((byte) 20);
                header.setRequestId(12345);
                protocolMessage.setHeader(header);
                protocolMessage.setBody(rpcRequest);

                // 使用 ProtocolMessageUtils 对 RpcRequest 进行编码
                try {
                    Buffer buffer = ProtocolMessageUtils.encode(protocolMessage);
                    System.out.println("----");
                    System.out.println("Sending request to server: " + buffer);
                    System.out.println(buffer.getByte(0)); // magic
                    System.out.println(buffer.getByte(1)); // version
                    System.out.println(buffer.getByte(2)); // serializer
                    System.out.println(buffer.getByte(3)); // type
                    System.out.println(buffer.getByte(4)); // status
                    System.out.println(buffer.getLong(5)); // id
                    System.out.println(buffer.getInt(13)); // bodyLength
                    System.out.println(buffer.getBytes(17, 17 + buffer.getInt(13)).toString());
                    System.out.println("----");
                    socket.write(buffer);
                } catch (IOException e) {
                    System.err.println("Error encoding request: " + e.getMessage());
                }

                // 接收并打印服务器响应
                socket.handler(buffer -> {
                    System.out.println("Received response from server: " + buffer.toString());
                });
            } else {
                System.err.println("Failed to connect to server");
            }
        });
    }
}
