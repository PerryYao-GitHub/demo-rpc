package com.ypy.rpc.registry;

import com.ypy.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryServiceCache {
    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceCache) {
        serviceCache.put(serviceKey, newServiceCache);
    }

    List<ServiceMetaInfo> readCache(String serviceKey) {
        return serviceCache.get(serviceKey);
    }

    void clearCache(String serviceKey) {
        serviceCache.remove(serviceKey);
    }
}
