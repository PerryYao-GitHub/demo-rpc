package com.ypy.rpc.fault.tolerant;

import com.ypy.rpc.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
