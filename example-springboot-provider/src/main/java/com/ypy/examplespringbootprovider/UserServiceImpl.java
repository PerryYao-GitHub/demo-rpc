package com.ypy.examplespringbootprovider;

import com.ypy.common.model.User;
import com.ypy.common.service.UserService;
import com.ypy.rpcspringbootstarter.anno.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        return user;
    }
}
