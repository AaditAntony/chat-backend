package com.aadit.chatapp.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/echo")
    public String echo() {
        return "Backend is working! Time: " + LocalDateTime.now();
    }

    @PostMapping("/simulate-message")
    public String simulatePrivateMessage() {
        System.out.println("🧪 TEST: Simulated message received");
        return "Test message simulation completed";
    }
}