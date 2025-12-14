package com.aadit.chatapp.backend.controller;

import com.aadit.chatapp.backend.dto.WebSocketMessageDto;
import com.aadit.chatapp.backend.entity.PrivateMessage;
import com.aadit.chatapp.backend.service.PrivateMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class PrivateChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final PrivateMessageService privateMessageService;

    public PrivateChatController(SimpMessagingTemplate messagingTemplate,
                                 PrivateMessageService privateMessageService) {
        this.messagingTemplate = messagingTemplate;
        this.privateMessageService = privateMessageService;
    }

    // ============ EXISTING METHOD (KEEP THIS AS IS) ============
    @MessageMapping("/private.send")
    public void sendPrivateMessage(PrivateMessage message, Principal principal) {
        // KEEP ALL YOUR EXISTING CODE HERE - DON'T CHANGE THIS
        System.out.println("🎯🎯🎯 PRIVATE MESSAGE RECEIVED AT BACKEND 🎯🎯🎯");
        System.out.println("📍 Principal: " + (principal != null ? principal.getName() : "null"));
        System.out.println("📍 Sender: " + message.getSender());
        System.out.println("📍 Receiver: " + message.getReceiver());
        System.out.println("📍 Content: " + message.getContent());
        // ... rest of your existing code ...
    }

    // ============ NEW METHOD 1: For Flutter Public Messages ============
    @MessageMapping("/chat.sendPublic")
    public void handlePublicMessage(WebSocketMessageDto messageDto, Principal principal) {
        System.out.println("📨 [FLUTTER] PUBLIC MESSAGE RECEIVED");
        System.out.println("📍 Sender: " + messageDto.getSender());
        System.out.println("📍 Content: " + messageDto.getContent());
        System.out.println("📍 Type: " + messageDto.getType());

        // Validate authentication
        validateSender(principal, messageDto.getSender());

        // Ensure type is PUBLIC
        messageDto.setType("PUBLIC");

        // Set timestamp if not set
        if (messageDto.getTimestamp() == null) {
            messageDto.setTimestamp(LocalDateTime.now());
        }

        // Broadcast to all connected clients
        messagingTemplate.convertAndSend("/topic/public", messageDto);
        System.out.println("✅ Public message broadcasted to /topic/public");
    }

    // ============ NEW METHOD 2: For Flutter Private Messages ============
    @MessageMapping("/chat.sendPrivate")
    public void handlePrivateMessage(WebSocketMessageDto messageDto, Principal principal) {
        System.out.println("📨 [FLUTTER] PRIVATE MESSAGE RECEIVED");
        System.out.println("📍 From: " + messageDto.getSender() + " → To: " + messageDto.getReceiver());
        System.out.println("📍 Content: " + messageDto.getContent());
        System.out.println("📍 Type: " + messageDto.getType());

        // Validate authentication
        validateSender(principal, messageDto.getSender());

        // Validate receiver
        if (messageDto.getReceiver() == null || messageDto.getReceiver().trim().isEmpty()) {
            System.out.println("❌ ERROR: Receiver is required for private messages");
            return;
        }

        // Ensure type is PRIVATE
        messageDto.setType("PRIVATE");

        // Set timestamp if not set
        if (messageDto.getTimestamp() == null) {
            messageDto.setTimestamp(LocalDateTime.now());
        }

        // Save to database as PrivateMessage entity
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setSender(messageDto.getSender());
        privateMessage.setReceiver(messageDto.getReceiver());
        privateMessage.setContent(messageDto.getContent());
        privateMessage.setTimestamp(messageDto.getTimestamp());

        try {
            // Save to DB
            PrivateMessage saved = privateMessageService.saveMessage(privateMessage);
            System.out.println("💾 Saved to DB with ID: " + saved.getId());

            // Send to receiver (using same format as web clients)
            String receiverTopic = "/topic/private." + saved.getReceiver();
            messagingTemplate.convertAndSend(receiverTopic, saved);
            System.out.println("📤 Sent to receiver: " + receiverTopic);

            // Also send to sender for chat history
            String senderTopic = "/topic/private." + saved.getSender();
            messagingTemplate.convertAndSend(senderTopic, saved);
            System.out.println("📤 Sent to sender: " + senderTopic);

            System.out.println("✅ Flutter private message delivered successfully");

        } catch (Exception e) {
            System.out.println("❌ Error saving message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============ NEW METHOD 3: For user join notifications ============
    @MessageMapping("/chat.user.join")
    public void handleUserJoin(WebSocketMessageDto messageDto, Principal principal) {
        System.out.println("👤 USER JOINED: " + messageDto.getSender());

        // Create join notification message
        WebSocketMessageDto joinMessage = new WebSocketMessageDto();
        joinMessage.setSender("System");
        joinMessage.setContent(messageDto.getSender() + " has joined the chat");
        joinMessage.setType("PUBLIC");
        joinMessage.setTimestamp(LocalDateTime.now());

        // Broadcast join notification
        messagingTemplate.convertAndSend("/topic/public", joinMessage);
        System.out.println("✅ Join notification sent");
    }

    // ============ HELPER METHOD ============
    private void validateSender(Principal principal, String senderUsername) {
        if (principal == null) {
            System.out.println("❌ ERROR: No principal - user not authenticated!");
            throw new AccessDeniedException("User not authenticated");
        }

        if (!principal.getName().equals(senderUsername)) {
            System.out.println("❌ ERROR: Authentication failed!");
            System.out.println("❌ Principal name: " + principal.getName());
            System.out.println("❌ Message sender: " + senderUsername);
            throw new AccessDeniedException("Sender does not match authenticated user");
        }

        System.out.println("✅ Authentication successful for: " + senderUsername);
    }
}