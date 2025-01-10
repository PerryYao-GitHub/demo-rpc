package com.ypy.rpc;

import com.ypy.rpc.config.RegistryConfig;
import com.ypy.rpc.config.RpcConfig;
import com.ypy.rpc.registry.Registry;
import com.ypy.rpc.registry.RegistryFactory;
import com.ypy.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * just like a holder, store the global variables that would be used
 */
@Slf4j
public class RpcApplication {
    // store config
    private static volatile RpcConfig rpcConfig;

    /**
     * init, you can transfer self-defined config
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // registry init
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);
    }

    /**
     * init
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // config load fail, use default config
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
