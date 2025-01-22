package com.ypy.rpc.config;

import com.ypy.rpc.fault.retry.RetryStrategy;
import com.ypy.rpc.fault.retry.RetryStrategyKeys;
import com.ypy.rpc.loadbalancer.LoadBalancerKeys;
import com.ypy.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC Config, allow user to config more details
 */
@Data
public class RpcConfig {
    /**
     * rpc name
     */
    private String name = "rpc";

    /**
     * version number
     */
    private String version = "1.0";

    /**
     * server host name
     */
    private String serverHost = "localhost";

    /**
     * port number
     */
    private Integer serverPort = 8080;

    /**
     * start with mock?
     */
    private boolean mock = false;

    /**
     * serializer
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * registry
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * loadbalancer
     */
    private String loadbalancer = LoadBalancerKeys.ROUND_ROBIN;

    private String retryStrategy = RetryStrategyKeys.FIXED_INTERVAL;

}
