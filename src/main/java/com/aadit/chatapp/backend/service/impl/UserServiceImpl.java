package com.aadit.chatapp.backend.service.impl;

import com.aadit.chatapp.backend.entity.User;
import com.aadit.chatapp.backend.repository.UserRepository;
import com.aadit.chatapp.backend.service.Userservice;

public class UserServiceImpl implements Userservice {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("username already exists");
        }
        User user= new User();
        user.setUsername(username);
        user.setPassword(password);
        return userRepository.save(user);
    }
}
