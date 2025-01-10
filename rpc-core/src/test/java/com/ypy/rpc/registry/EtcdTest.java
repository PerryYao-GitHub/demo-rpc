package com.ypy.rpc.registry;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * it is a test for the etcd client of java
 */
public class EtcdTest {
    @Test
    public void etcdTest() throws ExecutionException, InterruptedException {
        Client cli = Client.builder().endpoints("http://localhost:2375").build();
        // etcd --listen-client-urls http://localhost:2375 --advertise-client-urls http://localhost:2375
        // 使用以上命令开启 etcd 服务
        KV kvCli = cli.getKVClient();
        ByteSequence key = ByteSequence.from("leaders".getBytes());
        ByteSequence val = ByteSequence.from("[\"毛泽东\", \"邓小平\"]".getBytes());

        kvCli.put(key, val).get();

        CompletableFuture<GetResponse> getFuture = kvCli.get(key);

        GetResponse resp = getFuture.get();

        for (KeyValue kv : resp.getKvs()) {
            System.out.println("Key: " + kv.getKey());
            System.out.println("Value: " + kv.getValue());
        }

        kvCli.delete(key).get();
    }
}
