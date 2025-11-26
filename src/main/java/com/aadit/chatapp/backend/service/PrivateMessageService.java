package com.aadit.chatapp.backend.service;

import com.aadit.chatapp.backend.entity.PrivateMessage;

import java.util.List;

public interface PrivateMessageService {

    PrivateMessage saveMessage(PrivateMessage message);

    List<PrivateMessage> getChatHistory(String user1, String user2);
}
