package com.aadit.chatapp.backend.controller;

import com.aadit.chatapp.backend.entity.PrivateMessage;
import com.aadit.chatapp.backend.service.PrivateMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class PrivateChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final PrivateMessageService privateMessageService;

    @MessageMapping("/private.send")
    public void sendPrivateMessage(PrivateMessage message, Principal principal) {
        System.out.println("🎯🎯🎯 PRIVATE MESSAGE RECEIVED AT BACKEND 🎯🎯🎯");
        System.out.println("📍 Principal: " + (principal != null ? principal.getName() : "null"));
        System.out.println("📍 Sender: " + message.getSender());
        System.out.println("📍 Receiver: " + message.getReceiver());
        System.out.println("📍 Content: " + message.getContent());

        // Validate that the sender matches the authenticated user
        if (principal == null) {
            System.out.println("❌ ERROR: No principal found - user not authenticated!");
            throw new AccessDeniedException("User not authenticated");
        }

        if (!principal.getName().equals(message.getSender())) {
            System.out.println("❌ ERROR: Sender authentication failed!");
            System.out.println("❌ Principal name: " + principal.getName());
            System.out.println("❌ Message sender: " + message.getSender());
            throw new AccessDeniedException("Sender does not match authenticated user");
        }

        System.out.println("✅ Authentication successful!");

        // Set timestamp if not set
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
            System.out.println("✅ Timestamp set to: " + message.getTimestamp());
        }

        try {
            // 1. Save private message to DB
            System.out.println("💾 Saving message to database...");
            PrivateMessage saved = privateMessageService.saveMessage(message);
            System.out.println("✅ Message saved to DB with ID: " + saved.getId());

            // 2. Send to receiver ONLY (FIX: removed sending to sender)
            String receiverTopic = "/topic/private." + saved.getReceiver();
            System.out.println("📤 Sending to RECEIVER topic: " + receiverTopic);

            messagingTemplate.convertAndSend(receiverTopic, saved);
            System.out.println("✅ Message sent to RECEIVER successfully!");

            // 3. DO NOT send to sender - they will see it in their own UI
            // The sender already knows they sent the message

            System.out.println("🎯🎯🎯 MESSAGE PROCESSING COMPLETED 🎯🎯🎯");

        } catch (Exception e) {
            System.out.println("❌❌❌ ERROR PROCESSING MESSAGE: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ============ PUBLIC MESSAGES - FIX DUPLICATES ============
    @MessageMapping("/sendMessage")
    public void handlePublicMessage(
            com.aadit.chatapp.backend.model.ChatMessage message,
            Principal principal) {

        System.out.println("📨 [WEB] PUBLIC MESSAGE RECEIVED");
        System.out.println("📍 From: " + message.getSender());
        System.out.println("📍 Content: " + message.getContent());

        // Broadcast to all connected clients (including sender)
        // This is fine - sender should see their own message once
        messagingTemplate.convertAndSend("/topic/messages", message);
        System.out.println("✅ Public message broadcasted to /topic/messages");
    }
}