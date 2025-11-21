package com.aadit.chatapp.backend.service;

import com.aadit.chatapp.backend.entity.Message;

public interface MessageService {
    Message save(Message message);
}
