package com.ypy.rpc.fault.tolerant;

import com.ypy.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("FailSafeTolerantStrategy: Service Wrong");
        return new RpcResponse();
    }
}
