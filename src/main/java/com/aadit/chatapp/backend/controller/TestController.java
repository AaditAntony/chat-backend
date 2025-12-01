package com.aadit.chatapp.backend.controller;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // 1. Basic connectivity test
    @GetMapping("/echo")
    public String echo() {
        return "✅ Backend is working! Time: " + LocalDateTime.now();
    }

    // 2. Test database connection
    @GetMapping("/db-test")
    public Map<String, String> dbTest() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "Database connection test");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    // 3. Echo back any message (for testing POST)
    @PostMapping("/echo-message")
    public Map<String, String> echoMessage(@RequestBody Map<String, String> message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "Message received");
        response.put("your_message", message.get("text"));
        response.put("timestamp", LocalDateTime.now().toString());
        System.out.println("📨 Test message received: " + message.get("text"));
        return response;
    }

    // 4. Test authentication (simulate login)
    @PostMapping("/simulate-login")
    public Map<String, String> simulateLogin(@RequestBody Map<String, String> credentials) {
        Map<String, String> response = new HashMap<>();
        String username = credentials.get("username");
        String password = credentials.get("password");

        response.put("status", "Login simulation");
        response.put("username", username);
        response.put("message", "This is just a test - no real authentication");
        response.put("timestamp", LocalDateTime.now().toString());

        System.out.println("🔐 Simulated login attempt for user: " + username);
        return response;
    }

    // 5. Get system info
    @GetMapping("/system-info")
    public Map<String, Object> systemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Chat Application Backend");
        info.put("status", "Running");
        info.put("timestamp", LocalDateTime.now().toString());
        info.put("java_version", System.getProperty("java.version"));
        info.put("spring_boot_version", "3.x"); // You'd get this from pom.xml

        return info;
    }
}