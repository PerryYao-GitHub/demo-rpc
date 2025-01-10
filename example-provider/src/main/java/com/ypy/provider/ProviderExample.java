package com.ypy.provider;

import com.ypy.common.service.BookService;
import com.ypy.common.service.UserService;
import com.ypy.provider.service.impl.BookServiceImpl;
import com.ypy.provider.service.impl.UserServiceImpl;
import com.ypy.rpc.RpcApplication;
import com.ypy.rpc.config.RegistryConfig;
import com.ypy.rpc.config.RpcConfig;
import com.ypy.rpc.model.ServiceMetaInfo;
import com.ypy.rpc.registry.LocalRegistry;
import com.ypy.rpc.registry.Registry;
import com.ypy.rpc.registry.RegistryFactory;
import com.ypy.rpc.server.HttpServer;
import com.ypy.rpc.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        RpcApplication.init(); // rpc init
        // Local Registry
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class); // register service, make user service can be used by consumer through rpc
        LocalRegistry.register(BookService.class.getName(), BookServiceImpl.class);

        // Registry Factory
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // start web server
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
