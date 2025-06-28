package com.example.websocketdemo.repository;

import com.example.websocketdemo.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Basic query methods
    List<Message> findByReceiver(String receiver);
    List<Message> findByReceiverIsNull();
    List<Message> findBySender(String sender);
    
    // Paginated public/private messages (without ordering)
    Page<Message> findByReceiverIsNull(Pageable pageable);
    Page<Message> findByReceiver(String receiver, Pageable pageable);

    // Ordered message history
    List<Message> findByReceiverIsNullOrderByTimestampDesc();
    List<Message> findByReceiverOrderByTimestampDesc(String receiver);
    List<Message> findBySenderOrderByTimestampDesc(String sender);
    
    // Paginated + ordered message history
    Page<Message> findByReceiverIsNullOrderByTimestampDesc(Pageable pageable);
    Page<Message> findByReceiverOrderByTimestampDesc(String receiver, Pageable pageable);
}
