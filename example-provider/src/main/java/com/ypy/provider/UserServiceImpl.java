package com.ypy.provider;


import com.ypy.common.service.UserService;
import com.ypy.common.model.User;

/**
 * implement user service
 */
public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println("User: " + user.getName());
        return user;
    }
}
