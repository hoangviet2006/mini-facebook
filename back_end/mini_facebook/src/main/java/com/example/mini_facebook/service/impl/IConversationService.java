package com.example.mini_facebook.service.impl;

import com.example.mini_facebook.dto.ChatMessageRequest;
import com.example.mini_facebook.dto.ConversationResponse;
import com.example.mini_facebook.dto.MessageResponse;
import com.example.mini_facebook.model.Conversation;
import com.example.mini_facebook.model.Message;
import com.example.mini_facebook.model.User;

import java.util.List;

public interface IConversationService {
    Conversation getOrCreateConversation(User userA, User userB);
    List<ConversationResponse> getInbox(User currentUser);
    List<MessageResponse> getMessage(int conversationId, User current);
    MessageResponse chat(ChatMessageRequest request,User sender);
    long messageNotification(long idReceiver);
    void seenMessage(int idConversation,long idReceiver);
}
