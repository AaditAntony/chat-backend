package com.aadit.chatapp.backend.controller;

import com.aadit.chatapp.backend.entity.PrivateMessage;
import com.aadit.chatapp.backend.service.PrivateMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class PrivateChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final PrivateMessageService privateMessageService;
    private final SimpUserRegistry simpUserRegistry;

    public PrivateChatController(SimpMessagingTemplate messagingTemplate,
                                 PrivateMessageService privateMessageService,
                                 SimpUserRegistry simpUserRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.privateMessageService = privateMessageService;
        this.simpUserRegistry = simpUserRegistry;
    }

    @MessageMapping("/private.send")
    public void sendPrivateMessage(PrivateMessage message, Principal principal) {

        System.out.println("=== PRIVATE MESSAGE DEBUG ===");
        System.out.println("Principal: " + (principal != null ? principal.getName() : "null"));
        System.out.println("Message sender: " + message.getSender());
        System.out.println("Message receiver: " + message.getReceiver());
        System.out.println("Message content: " + message.getContent());

        // Print connected users
        System.out.println("Connected users:");
        for (SimpUser user : simpUserRegistry.getUsers()) {
            System.out.println(" - " + user.getName());
        }

        // Validate that the sender matches the authenticated user
        if (principal == null || !principal.getName().equals(message.getSender())) {
            System.out.println("ERROR: Sender authentication failed!");
            throw new AccessDeniedException("Sender does not match authenticated user");
        }

        // Set timestamp if not set
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }

        // 1. Save private message to DB
        PrivateMessage saved = privateMessageService.saveMessage(message);
        System.out.println("Message saved to DB with ID: " + saved.getId());

        // TEMPORARY: Send to public topic for testing
        String publicTopic = "/topic/private." + saved.getReceiver();
        System.out.println("TEMPORARY: Sending to public topic: " + publicTopic);

        // Send to a public topic that only the receiver subscribes to
        messagingTemplate.convertAndSend(publicTopic, saved);
        System.out.println("✓ Message sent to public topic successfully!");

        System.out.println("=== END DEBUG ===");
    }
}