package com.example.mini_facebook.repository;

import com.example.mini_facebook.model.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFriendsRepository extends JpaRepository<Friends,Integer> {
    Optional<Friends> findBySender_IdAndReceiver_Id(Long senderId, Long receiverId);
    @Query(value = "select * from friend f where f.receiver_id = ?1 and f.status = \"PENDING\"",nativeQuery = true)
    List<Friends> findFriendRequestList(Long receiverId);
    List<Friends> findBySender_IdOrReceiver_Id(Long senderId, Long receiverId);
    @Transactional
    @Modifying
    long deleteBySender_IdAndReceiver_Id(Long senderId, Long receiverId);
    @Query(value = "select * from friend f where f.status =\"APPROVED\" and   (f.sender_id = ?1 OR f.receiver_id = ?2);",nativeQuery = true)
    List<Friends> findALlFriendByCurrentUser(Long idSender,Long idReceiver);
}
