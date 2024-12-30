package com.ypy.rpc.server;

/**
 * http server interface, define unified method of starting server
 */
public interface HttpServer {
    /**
     * start server
     *
     * @param port
     */
    void doStart(int port);
}
