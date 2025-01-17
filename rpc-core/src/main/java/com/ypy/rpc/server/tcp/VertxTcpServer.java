package com.ypy.rpc.server.tcp;

import com.ypy.rpc.server.Server;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpServer implements Server {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        NetServer server = vertx.createNetServer();
        server.connectHandler(new TcpServerHandler());
        server.listen(port, res -> {
            if (res.succeeded()) System.out.println("Tcp server started on port " + port);
            else System.out.println("Tcp server failed to start on port " + port);
        });
    }
}
