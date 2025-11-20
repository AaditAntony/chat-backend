package com.aadit.chatapp.backend.model;

import lombok.Data;

@Data
public class ChatMessage {
    private String sender;
    private String content;
}
