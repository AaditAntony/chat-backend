package com.aadit.chatapp.backend.dto;
import com.aadit.chatapp.backend.model.MessageType;
import lombok.Data;

@Data
public class ChatMessage {
    private String sender;
    private String receiver; // null for public messages
    private String content;
    private MessageType type;
}