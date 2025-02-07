package com.ypy.provider;

import com.ypy.common.service.BookService;
import com.ypy.common.service.UserService;
import com.ypy.provider.service.impl.BookServiceImpl;
import com.ypy.provider.service.impl.UserServiceImpl;
import com.ypy.rpc.RpcApplication;
import com.ypy.rpc.bootstrap.ProviderBootStrap;
import com.ypy.rpc.config.RegistryConfig;
import com.ypy.rpc.config.RpcConfig;
import com.ypy.rpc.model.ServiceMetaInfo;
import com.ypy.rpc.model.ServiceRegisterInfo;
import com.ypy.rpc.registry.LocalRegistry;
import com.ypy.rpc.registry.Registry;
import com.ypy.rpc.registry.RegistryFactory;
import com.ypy.rpc.server.Server;
import com.ypy.rpc.server.http.VertxHttpServer;
import com.ypy.rpc.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderExample {
    public static void main(String[] args) {
        /*
        RpcApplication.init(); // rpc init
        // Local Registry
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class); // register service, make user service can be used by consumer through rpc
        LocalRegistry.register(BookService.class.getName(), BookServiceImpl.class);

        // Registry Factory
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());

        ServiceMetaInfo userServiceMetaInfo = new ServiceMetaInfo();
        userServiceMetaInfo.setServiceName(UserService.class.getName());
        userServiceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        userServiceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(userServiceMetaInfo);
        } catch (Exception e) { throw new RuntimeException(e); }

        ServiceMetaInfo bookServiceMetaInfo = new ServiceMetaInfo();
        bookServiceMetaInfo.setServiceName(BookService.class.getName());
        bookServiceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        bookServiceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(bookServiceMetaInfo);
        } catch (Exception e) { throw new RuntimeException(e); }

        // start web server (http server)
//        Server httpServer = new VertxHttpServer();
//        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

        // start web server (tcp server)
        Server tcpServer = new VertxTcpServer();
        tcpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

         */

        // use boostrap
        ServiceRegisterInfo<UserService> userServiceServiceRegisterInfo = new ServiceRegisterInfo<>();
        userServiceServiceRegisterInfo.setServiceName(UserService.class.getName());
        userServiceServiceRegisterInfo.setImplClass(UserServiceImpl.class);
        ServiceRegisterInfo<BookService> bookServiceServiceRegisterInfo = new ServiceRegisterInfo<>();
        bookServiceServiceRegisterInfo.setServiceName(BookService.class.getName());
        bookServiceServiceRegisterInfo.setImplClass(BookServiceImpl.class);

        ProviderBootStrap.init(List.of(userServiceServiceRegisterInfo, bookServiceServiceRegisterInfo));
    }
}
