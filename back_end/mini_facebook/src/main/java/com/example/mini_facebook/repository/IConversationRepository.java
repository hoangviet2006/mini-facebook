package com.example.mini_facebook.repository;

import com.example.mini_facebook.model.Conversation;
import com.example.mini_facebook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IConversationRepository extends JpaRepository<Conversation,Integer> {
    Optional<Conversation> findByUser1_IdAndUser2_Id(Long user1Id, Long user2Id);

    Optional<Conversation> findByUser2_IdAndUser1_Id(Long user2Id, Long user1Id);

    List<Conversation> findByUser1OrUser2(User user1, User user2);

    Optional<Conversation> findById(int id);

}
