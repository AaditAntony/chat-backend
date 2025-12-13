package com.aadit.chatapp.backend.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String sender;
    private String receiver; // null for public messages
    private String content;
    private MessageType type;
    private LocalDateTime timestamp = LocalDateTime.now();
}