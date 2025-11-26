package com.aadit.chatapp.backend.repository;

import com.aadit.chatapp.backend.entity.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {

    // Fetch all messages between user A and user B
    List<PrivateMessage> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
            String sender1, String receiver1,
            String sender2, String receiver2
    );
}
