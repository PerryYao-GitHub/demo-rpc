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
    public static void main(String[] args) throws InterruptedException {
        RpcApplication.init();

        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

        BookService bookService = ServiceProxyFactory.getProxy(BookService.class);
        UserService userService = ServiceProxyFactory.getProxy(UserService.class); // using dynamic proxy to retrieve proxy objects from RPC proxy factory

        Book book = bookService.getBookById(8964L); // in Book, there is a List field, so hessian serializer can't work here
        System.out.println(book);

        User user = new User();
        user.setName("毛泽东");
        User newUser = userService.getUser(user);
        if (newUser != null) System.out.println(newUser.getName());
        else System.out.println("user is null");

//        Thread.sleep(30 * 1000L);

        User user1 = new User();
        user1.setName("邓小平");
        User newUser1 = userService.getUser(user1);
        if (newUser1 != null) System.out.println(newUser1.getName());
        else System.out.println("user1 is null");

        User user2 = new User();
        user2.setName("江泽民");
        User newUser2 = userService.getUser(user2);
        if (newUser2 != null) System.out.println(newUser2.getName());
        else System.out.println("user2 is null");

        int number = userService.getNumber();
        System.out.println(number); // output is 0, not 8964, it means that it is mock data
    }
}
