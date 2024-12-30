package com.ypy.consumer;

import com.ypy.common.model.User;
import com.ypy.common.service.UserService;
import com.ypy.rpc.config.RpcConfig;
import com.ypy.rpc.proxy.ServiceProxyFactory;
import com.ypy.rpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
        UserService userService = ServiceProxyFactory.getProxy(UserService.class); // using dynamic proxy to retrieve proxy objects from RPC proxy factory
        User user = new User();
        user.setName("mao");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user is null");
        }
    }
}
