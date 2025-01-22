package com.ypy.rpc.fault.retry;

import com.ypy.rpc.model.RpcResponse;
import org.junit.Test;

public class RetryStrategyTest {
    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("Test Retry");
                throw new RuntimeException("Mock Retry Fail");
            });
        } catch (Exception e) {
            System.out.println("Retry fail");
            e.printStackTrace();
        }
    }
}
