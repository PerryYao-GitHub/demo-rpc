package com.ypy.rpc.model;

import cn.hutool.core.util.StrUtil;
import com.ypy.rpc.RpcConstant;
import lombok.Data;

/**
 * service registry info
 */
@Data
public class ServiceMetaInfo {
    private String serviceName;

    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    private String serviceHost;

    private  int servicePort;

    private String serviceGroup = "default"; // TODO

    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    public String getServiceAddr() {
        String s = String.format("%s:%s", serviceHost, servicePort);
        if (!StrUtil.contains(serviceHost, "http")) s = "http://" + s;
        return s;
    }
}
