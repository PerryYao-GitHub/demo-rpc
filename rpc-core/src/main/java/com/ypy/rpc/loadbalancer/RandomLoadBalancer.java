package com.ypy.rpc.loadbalancer;

import com.ypy.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {
    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int sz = serviceMetaInfoList.size();
        if (sz == 0) return null;
        if (sz == 1) return serviceMetaInfoList.get(0);

        return serviceMetaInfoList.get(random.nextInt(sz));
    }
}
