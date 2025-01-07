package com.ypy.consumer;

import com.ypy.common.model.Book;
import com.ypy.common.model.User;
import com.ypy.common.service.BookService;
import com.ypy.common.service.UserService;
import com.ypy.rpc.RpcApplication;
import com.ypy.rpc.config.RpcConfig;
import com.ypy.rpc.proxy.ServiceProxyFactory;
import com.ypy.rpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        RpcApplication.init();

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
        int number = userService.getNumber();
        System.out.println(number); // output is 0, not 8964, it means that it is mock data

        BookService bookService = ServiceProxyFactory.getProxy(BookService.class);
        Book book = bookService.getBookById(8964L); // in Book, there is a List field, so hessian serializer can't work here
        System.out.println(book);
    }
}
