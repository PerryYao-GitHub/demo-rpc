package com.ypy.consumer;

import com.ypy.common.model.User;
import com.ypy.common.service.UserService;
import com.ypy.rpc.proxy.ServiceProxyFactory;

/**
 * consumer starter
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
//        UserService userService = new UserServiceProxy(); // static proxy, not recommend
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
