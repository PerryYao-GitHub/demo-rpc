package com.ypy.rpc.config;

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
}
