package com.ypy.rpc.loadbalancer;

import com.ypy.rpc.spi.SpiLoader;

public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    public static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    public static LoadBalancer getInstance(String key) { return SpiLoader.getInstance(LoadBalancer.class, key); }
}
