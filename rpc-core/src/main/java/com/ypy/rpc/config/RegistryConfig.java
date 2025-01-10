package com.ypy.rpc.config;

import lombok.Data;

@Data
public class RegistryConfig {
    private String registry = "etcd";
    private String address = "http://127.0.0.1:2375";
    private String username;
    private String password;
    private Long timeout = 10000L;
}
