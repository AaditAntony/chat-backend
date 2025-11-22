package com.aadit.chatapp.backend.service.impl;

import com.aadit.chatapp.backend.entity.Message;
import com.aadit.chatapp.backend.repository.MessageRepository;
import com.aadit.chatapp.backend.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }
}
