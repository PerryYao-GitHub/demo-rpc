package com.ypy.rpc.loadbalancer;

import com.ypy.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer {
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    private static final int VIRTUAL_NODE_NUM = 10;

    private int getHash(Object key) { return key.hashCode(); }

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
//        System.out.println("ConsistentHashLoadBalancer select");
        if (serviceMetaInfoList.isEmpty()) return null;

        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i ++) {
                int hash = getHash(serviceMetaInfo.getServiceAddr() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        int hash = getHash(requestParams);

        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) entry = virtualNodes.firstEntry();
        return entry.getValue();
    }
}
