package com.aadit.chatapp.backend.controller;

import com.aadit.chatapp.backend.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/sendMessage")      // frontend sends to -> /app/sendMessage
    @SendTo("/topic/messages")           // backend broadcasts to -> /topic/messages
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }
}
