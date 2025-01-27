package com.ypy.rpc.fault.tolerant;

import com.ypy.rpc.model.RpcResponse;

import java.util.Map;

public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("FailFastTolerantStrategy: Service Wrong", e);
    }
}
