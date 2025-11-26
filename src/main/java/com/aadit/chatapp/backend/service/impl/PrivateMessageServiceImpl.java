package com.aadit.chatapp.backend.service.impl;

import com.aadit.chatapp.backend.entity.PrivateMessage;
import com.aadit.chatapp.backend.repository.PrivateMessageRepository;
import com.aadit.chatapp.backend.service.PrivateMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {

    private final PrivateMessageRepository privateMessageRepository;

    public PrivateMessageServiceImpl(PrivateMessageRepository privateMessageRepository) {
        this.privateMessageRepository = privateMessageRepository;
    }

    @Override
    public PrivateMessage saveMessage(PrivateMessage message) {
        return privateMessageRepository.save(message);
    }

    @Override
    public List<PrivateMessage> getChatHistory(String user1, String user2) {
        return privateMessageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                user1, user2,
                user2, user1
        );
    }
}
