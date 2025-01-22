package com.ypy.rpc.loadbalancer;

import com.ypy.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int sz = serviceMetaInfoList.size();
        if (sz == 0) return null;
        if (sz == 1) return serviceMetaInfoList.get(0);

        int idx = currentIndex.getAndIncrement() % sz;
        return serviceMetaInfoList.get(idx);
    }
}
