package com.aadit.chatapp.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivateMessage {
    private String sender;
    private String receiver;
    private String content;
}
