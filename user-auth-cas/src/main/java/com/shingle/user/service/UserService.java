package com.shingle.user.service;


import com.shingle.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // 模拟数据库中的用户数据
    private static final User[] USERS = {
            new User("user", "user"),
            new User("admin", "admin")
    };

    public User authenticate(String username, String password) {
        for (User user : USERS) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}

