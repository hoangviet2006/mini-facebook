package com.example.mini_facebook.repository;

import com.example.mini_facebook.model.Conversation;
import com.example.mini_facebook.model.Message;
import com.example.mini_facebook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IMessageRepository extends JpaRepository<Message,Integer> {
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);
    @Query(value = "select count(*) from message m where m.is_seen = false and receiver_id=?1",nativeQuery = true)
    long countByMessage(long receiverId);

    @Modifying
    @Transactional
    @Query(value = """
    update message 
    set is_seen = true
    where is_seen = false
      and conversation_id = ?1
      and receiver_id = ?2
""", nativeQuery = true)
    int markSeen(int conversationId, long receiverId);
}
