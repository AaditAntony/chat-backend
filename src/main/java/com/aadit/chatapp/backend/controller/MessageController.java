package com.aadit.chatapp.backend.controller;

import com.aadit.chatapp.backend.entity.Message;
import com.aadit.chatapp.backend.service.MessageService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat.send")

    @SendTo("/topic/global")
    public Message broadcast(Message message) {
        // save message to DB
        Message saved = messageService.save(message);

        // return the saved message so all users receive it
        return saved;
    }
}
