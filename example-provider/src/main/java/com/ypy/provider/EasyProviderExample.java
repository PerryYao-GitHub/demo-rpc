package com.ypy.provider;

import com.ypy.common.service.UserService;
import com.ypy.provider.service.impl.UserServiceImpl;
import com.ypy.rpc.registry.LocalRegistry;
import com.ypy.rpc.server.Server;
import com.ypy.rpc.server.http.VertxHttpServer;

/**
 * provider starter
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class); // register service, make user service can be used by consumer through rpc
        Server httpServer = new VertxHttpServer(); // deploy web server
        httpServer.doStart(8080);
    }
}
