package com.ypy.rpc.tcp;

import com.ypy.rpc.model.RpcRequest;
import com.ypy.rpc.protocol.ProtocolMessage;
import com.ypy.rpc.protocol.ProtocolMessageTypeEnum;
import com.ypy.rpc.protocol.ProtocolMessageUtils;
import com.ypy.rpc.server.tcp.TcpServerHandler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class TcpServerClientTest1 {
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
        // 创建 TCP 服务器
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(socket -> {
            // 构造 parser，使用一个固定大小的头部来解析消息
            RecordParser parser = RecordParser.newFixed(8);  // 先读取 8 字节头
            parser.setOutput(new Handler<Buffer>() {
                // 初始化
                int size = -1;
                // 一次完整的读取（头 + 体）
                Buffer resultBuffer = Buffer.buffer();

                @Override
                public void handle(Buffer buffer) {
                    if (size == -1) {
                        // 读取消息体长度
                        if (buffer.length() >= 8) {
                            size = buffer.getInt(4);  // 获取消息体的长度
                            parser.fixedSizeMode(size);  // 调整解析器模式
                            resultBuffer.appendBuffer(buffer);  // 添加已接收的头信息
                        } else {
                            // 如果消息头还未完全接收，继续等待
                            resultBuffer.appendBuffer(buffer);
                        }
                    } else {
                        // 读取并拼接消息体
                        resultBuffer.appendBuffer(buffer);
                        if (resultBuffer.length() >= size) {
                            System.out.println("Server: " + resultBuffer.toString());  // 打印完整的消息
                            // 重置状态，等待下一个消息
                            parser.fixedSizeMode(8);  // 重新读取下一个 8 字节的头
                            size = -1;
                            resultBuffer = Buffer.buffer();  // 清空结果缓冲区
                        }
                    }
                }
            });

            socket.handler(parser);
        });

        // 启动 TCP 服务器并监听指定端口
        server.listen(PORT, result -> {
            if (result.succeeded()) {
                log.info("Server: TCP server started on port " + PORT);
            } else {
                log.error("Server: Failed to start TCP server: " + result.cause());
            }
        });
    }

    private static void startClient(Vertx vertx) {
        NetClient client = vertx.createNetClient();
        client.connect(PORT, "localhost", result -> {
            if (result.succeeded()) {
                System.out.println("Client: Connected to TCP Server");
                NetSocket socket = result.result();
                String str = "Hello, server!Hello, server!Hello, server!Hello, server!";

                // 循环发送消息
                for (int i = 0; i < 1000; i++) {
                    Buffer buffer = Buffer.buffer();
                    // 包括长度字段和实际数据
                    buffer.appendInt(0);  // 第一个 4 字节数据：可能是标识字段
                    buffer.appendInt(str.getBytes().length);  // 第二个 4 字节数据：消息体长度
                    buffer.appendBytes(str.getBytes());  // 消息体

                    socket.write(buffer);
                }
                socket.handler(buffer -> {
                    System.out.println("Client: Received response from server: " + buffer.toString());
                });
            } else {
                System.err.println("Client: Failed to connect to TCP server");
            }
        });
    }
}
