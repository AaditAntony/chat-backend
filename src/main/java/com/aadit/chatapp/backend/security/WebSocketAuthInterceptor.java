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

            // Extract token
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);
                System.out.println("🔍 Authorization header: " + authHeader);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    System.out.println("✅ Token extracted: " + token.substring(0, Math.min(20, token.length())) + "...");

                    try {
                        String username = jwtUtil.extractUsername(token);
                        System.out.println("🔑 Username from token: " + username);

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
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("❌ No Bearer token in header");
                }
            } else {
                System.out.println("❌ No Authorization header found");
            }
        }

        return message;
    }
}