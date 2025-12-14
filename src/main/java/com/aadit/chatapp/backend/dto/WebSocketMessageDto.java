package com.aadit.chatapp.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketMessageDto {
    private String sender;
    private String receiver; // null for public messages
    private String content;
    private String type; // "PUBLIC" or "PRIVATE"
    private LocalDateTime timestamp = LocalDateTime.now();
}