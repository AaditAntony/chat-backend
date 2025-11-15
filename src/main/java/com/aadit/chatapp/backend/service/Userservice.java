package com.aadit.chatapp.backend.service;

import com.aadit.chatapp.backend.entity.User;

public interface Userservice {
    User register(String username,String password);
}
