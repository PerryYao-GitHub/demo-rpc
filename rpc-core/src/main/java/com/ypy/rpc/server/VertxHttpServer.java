package com.ypy.rpc.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        /* // testing code; to test how the server handle request (asynchronous programming)
        server.requestHandler(request -> {
            System.out.println("Received request: " + request.method() + " " + request.uri());
            request.response()
                    .putHeader("content-type", "text/plain")
                    .end("Helle from Vert.x HTTP server!");
        });
        */
        server.requestHandler(new HttpServerHandler()); // use self-defined request handler (asynchronous programming)

        // start and listen the port
        server.listen(port, result -> {
           if (result.succeeded()) {
               System.out.println("Server is now listening on port " + port);
           } else {
               System.out.println("Failed to strat server: " + result.cause());
           }
        });
    }
}
