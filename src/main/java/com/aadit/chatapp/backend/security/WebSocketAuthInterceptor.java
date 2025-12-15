package com.aadit.chatapp.backend.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final MyUserDetailsService userDetailsService;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil, MyUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            System.out.println("🎯 WebSocket CONNECT received");

            // Extract token from Authorization header
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            String token = null;

            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);
                System.out.println("🔍 Authorization header: " + (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) : "null"));

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            // Also check for "token" header (some clients send this)
            if (token == null) {
                List<String> tokenHeaders = accessor.getNativeHeader("token");
                if (tokenHeaders != null && !tokenHeaders.isEmpty()) {
                    token = tokenHeaders.get(0);
                    System.out.println("✅ Found token in 'token' header");
                }
            }

            if (token != null) {
                try {
                    System.out.println("🔑 Validating token...");
                    String username = jwtUtil.extractUsername(token);
                    System.out.println("✅ Username from token: " + username);

                    if (jwtUtil.isTokenValid(token, username)) {
                        System.out.println("✅ Token is valid!");
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(auth);
                        accessor.setUser(auth);

                        System.out.println("✓ WebSocket user authenticated: " + username);
                    } else {
                        System.out.println("❌ Token validation failed!");
                    }
                } catch (Exception e) {
                    System.out.println("❌ Error during WebSocket auth: " + e.getMessage());
                }
            } else {
                System.out.println("⚠️ No token found - allowing connection for testing");
                // For testing, allow connection without token
                // In production, you should reject the connection
                accessor.setUser(() -> "anonymous");
            }
        }

        return message;
    }
}