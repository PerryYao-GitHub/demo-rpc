package com.ypy.rpc.bootstrap;

import com.ypy.rpc.RpcApplication;
import com.ypy.rpc.RpcConstant;
import com.ypy.rpc.config.RegistryConfig;
import com.ypy.rpc.config.RpcConfig;
import com.ypy.rpc.model.ServiceMetaInfo;
import com.ypy.rpc.model.ServiceRegisterInfo;
import com.ypy.rpc.registry.LocalRegistry;
import com.ypy.rpc.registry.Registry;
import com.ypy.rpc.registry.RegistryFactory;
import com.ypy.rpc.server.tcp.VertxTcpClient;
import com.ypy.rpc.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootStrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        RpcApplication.init(); // load config

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig(); // get config
        // register service for provider
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            // local register
            LocalRegistry.register(serviceRegisterInfo.getServiceName(), serviceRegisterInfo.getImplClass());

            // register into registry
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceRegisterInfo.getServiceName());
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

            serviceMetaInfo.setServiceGroup("default group");
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceRegisterInfo.getServiceName() + " register failed", e);
            }
        }
        // start server
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
