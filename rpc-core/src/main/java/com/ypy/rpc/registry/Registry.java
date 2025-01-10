package com.ypy.rpc.registry;

import com.ypy.rpc.config.RegistryConfig;
import com.ypy.rpc.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {
    /**
     * init
     *
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * register service (for Provider)
     *
     * @param serviceMetaInfo
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * unregister service (for Provider)
     *
     * @param serviceMetaInfo
     */
    void unregister(ServiceMetaInfo serviceMetaInfo);

    /**
     * search suitable service (for consumer)
     *
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * destroy whole registry
     */
    void destroy();
}
