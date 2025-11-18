package com.aadit.chatapp.backend.service;

import com.aadit.chatapp.backend.entity.User;

public interface UserService {
    User register(String username,String password);
    String login(String username,String password);
}
