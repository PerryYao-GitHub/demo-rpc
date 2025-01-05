package com.ypy.common.service;

import com.ypy.common.model.User;

/**
 * user service
 */
public interface UserService {
    /**
     * simulate get user
     *
     * @param user
     * @return User
     */
    User getUser(User user);

    /**
     * new method get number
     */
    default short getNumber() { return 8964; }
}
