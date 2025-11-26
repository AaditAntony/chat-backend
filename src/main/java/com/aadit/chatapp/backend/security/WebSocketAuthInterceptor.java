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
            String token = extractToken(accessor);

            if (token != null) {
                try {
                    String username = jwtUtil.extractUsername(token);
                    if (username != null && jwtUtil.isTokenValid(token, username)) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        // THIS IS CRITICAL: Set the authentication in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(auth);

                        // AND set it in the accessor for WebSocket session
                        accessor.setUser(auth);

                        System.out.println("✓ WebSocket authenticated user: " + username);
                    }
                } catch (Exception e) {
                    System.out.println("✗ WebSocket auth failed: " + e.getMessage());
                }
            }
        }
        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}