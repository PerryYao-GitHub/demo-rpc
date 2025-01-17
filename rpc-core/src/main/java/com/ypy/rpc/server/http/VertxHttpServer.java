package com.ypy.rpc.server.http;

import com.ypy.rpc.server.Server;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class VertxHttpServer implements Server {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(new HttpServerHandler()); // use self-defined request handler (asynchronous programming)

        // start and listen the port
        server.listen(port, result -> {
           if (result.succeeded()) {
               System.out.println("Server is now listening on port " + port);
           } else {
               System.out.println("Failed to start server: " + result.cause());
           }
        });
    }
}
