package com.example.mini_facebook.service.impl;

import com.example.mini_facebook.model.Friends;

import java.util.List;

public interface IFriendsService {
    void addFriend(long senderId,long receiverId);
    long cancelAddFriend(long senderId,long receiverId);
    List<Friends> findFriendRequestList(Long receiverId);
    boolean acceptAddFriend(long senderId,long receiverId);
    List<Friends> findAllFriendByCurrentId(Long idSender, Long idReceiver);
}
