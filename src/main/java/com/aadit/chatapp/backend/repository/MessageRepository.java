package com.aadit.chatapp.backend.repository;

import com.aadit.chatapp.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
