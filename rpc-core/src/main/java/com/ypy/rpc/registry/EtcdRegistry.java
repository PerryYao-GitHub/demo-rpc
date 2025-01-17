package com.ypy.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.ypy.rpc.config.RegistryConfig;
import com.ypy.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {
    private Client cli;

    private KV kvCli;

    private static final String ETCD_ROOT_PATH = "/rpc/";

    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    private final Set<String> watchingKeyNodeSet = new ConcurrentHashSet<>(); // [/rpc/com.ypy.common.service.BookService:1.0/localhost:8080, /rpc/com.ypy.common.service.UserService:1.0/localhost:8080]

    @Override
    public void init(RegistryConfig registryConfig) {
        cli = Client
                .builder()
                .endpoints(registryConfig.getAddress()) // Etcd Service Url Port
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvCli = cli.getKVClient();
        heartBeat(); // start heart beat
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        Lease leaseCli = cli.getLeaseClient();

        long leaseId = leaseCli.grant(30).get().getID(); // 30s lease

        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence val = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvCli.put(key, val, putOption).get();

        localRegisterNodeKeySet.add(registerKey); // add key node into local set
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvCli.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));
        localRegisterNodeKeySet.remove(registerKey); // remove key node from local set
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
//        System.out.println(watchingKeyNodeSet);
        // search in service cache first
        List<ServiceMetaInfo> cachedServiceMetaInfos = registryServiceCache.readCache(serviceKey);
        if (cachedServiceMetaInfos != null) return cachedServiceMetaInfos;

        // search in registry center
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> kvs = kvCli.get(
                    ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                    getOption
            ).get().getKvs();

            /*
            return kvs.stream()
                    .map(kv -> {
                        String val = kv.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(val, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());
             */
            // interpret service info list
            List<ServiceMetaInfo> serviceMetaInfoList = kvs.stream()
                    .map(kv -> {
                        String key = kv.getKey().toString(StandardCharsets.UTF_8);
                        watch(key);
                        /*
                        System.out.println(key); // /rpc/com.ypy.common.service.UserService:1.0/localhost:8080
                        System.out.println(serviceKey); // com.ypy.common.service.UserService:1.0
                        */
                        String val = kv.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(val, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());

            // write into cache
            registryServiceCache.writeCache(serviceKey, serviceMetaInfoList);
            System.out.printf("get %s service meta info from etcd \n", serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("getting service list failed", e);
        }
    }

    /**
     * execute the following process when JVM stop
     */
    @Override
    public void destroy() {
        System.out.println("destroy current node");

        for (String key: localRegisterNodeKeySet) {
            try {
                kvCli.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "destroy failed");
            }
        }

        if (kvCli != null) kvCli.close();
        if (cli != null) cli.close();
    }

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", new Task() { // every 10 seconds, update lease for all services provided by current Provider
            @Override
            public void execute() {
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> kvs = kvCli.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        if (CollUtil.isEmpty(kvs)) continue;
                        KeyValue kv = kvs.get(0);
                        String val = kv.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(val, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) { throw new RuntimeException(key + " updating lease failed", e); }
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceKeyNode) {
        Watch watchCli = cli.getWatchClient();
        if (watchingKeyNodeSet.add(serviceKeyNode)) {
            watchCli.watch(ByteSequence.from(serviceKeyNode, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        case DELETE:
                            registryServiceCache.clearCache(serviceKeyNode); break; // todo: transform serviceKeyNode to serviceKey !!!
                        case PUT:
                        default: break;
                    }
                }
            });
        }
    }
}
