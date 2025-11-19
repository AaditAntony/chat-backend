package com.aadit.chatapp.backend.controller;

import com.aadit.chatapp.backend.dto.LoginRequest;
import com.aadit.chatapp.backend.dto.RegisterRequest;
import com.aadit.chatapp.backend.dto.TokenResponse;
import com.aadit.chatapp.backend.entity.User;
import com.aadit.chatapp.backend.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request.getUsername(), request.getPassword());
    }
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        return new TokenResponse(token);
    }

}
