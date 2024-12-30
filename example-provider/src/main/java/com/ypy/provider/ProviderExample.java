package com.ypy.provider;

import com.ypy.common.service.UserService;
import com.ypy.rpc.RpcApplication;
import com.ypy.rpc.registry.LocalRegistry;
import com.ypy.rpc.server.HttpServer;
import com.ypy.rpc.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        RpcApplication.init(); // rpc init
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class); // register service, make user service can be used by consumer through rpc
        HttpServer httpServer = new VertxHttpServer(); // deploy web server
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
