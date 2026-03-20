package com.civiora.civiora.repositories;

import com.civiora.civiora.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepo extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findAllByOrderBySentAtAsc();
    List<ChatMessage> findByIdGreaterThanOrderBySentAtAsc(int lastId);
}
