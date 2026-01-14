package com.example.mini_facebook.service;

import com.example.mini_facebook.dto.ChatMessageRequest;
import com.example.mini_facebook.dto.ConversationResponse;
import com.example.mini_facebook.dto.MessageResponse;
import com.example.mini_facebook.exception.UserNotFoundException;
import com.example.mini_facebook.model.Conversation;
import com.example.mini_facebook.model.Message;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IConversationRepository;
import com.example.mini_facebook.repository.IMessageRepository;
import com.example.mini_facebook.repository.IUserRepository;
import com.example.mini_facebook.service.impl.IConversationService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService implements IConversationService {
    @Autowired
    private IConversationRepository conversationRepository;
    @Autowired
    private IMessageRepository messageRepository;
    @Autowired
    private IUserRepository userRepository;
    @Override
    public Conversation getOrCreateConversation(User userA, User userB) {
        long userId1 = Math.min(userA.getId(),userB.getId());
        long userId2 = Math.max(userA.getId(),userB.getId());
        Optional<Conversation> c = conversationRepository.findByUser1_IdAndUser2_Id(userId1,userId2);
        if (c.isPresent()) return c.get();
        Conversation conversation = new Conversation();
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUser1(userRepository.findUserById(userId1).orElseThrow(() -> new UserNotFoundException("User không tồn tại")));
        conversation.setUser2(userRepository.findUserById(userId2).orElseThrow(() -> new UserNotFoundException("User không tồn tại")));
        conversationRepository.save(conversation);
        return conversation;
    }

    @Override
    public List<ConversationResponse> getInbox(User currentUser) {
        List<Conversation> conversations = conversationRepository.findByUser1OrUser2(currentUser, currentUser);
        List<ConversationResponse> result = new ArrayList<>();
        for (Conversation c : conversations) {
            User otherUser;
            if (c.getUser1().getId().equals(currentUser.getId())) {
                otherUser = c.getUser2();
            } else {
                otherUser = c.getUser1();
            }
            ConversationResponse res = new ConversationResponse();
            res.setIdConversation(c.getId());
            res.setAvatar(otherUser.getAvatarUrl());
            res.setIdUser(otherUser.getId());
            res.setFullName(otherUser.getFullName());
            result.add(res);
        }
        return result;
    }

    @Override
    public List<MessageResponse> getMessage(int conversationId, User current) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đoạn chat"));
        if (!current.equals(conversation.getUser1())
            && !current.equals(conversation.getUser2())) {
            throw new AccessDeniedException("Bạn không thể xem tin nhắn");
        }
        List<Message> messages = messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
        List<MessageResponse> messageResponseList = new ArrayList<>();
        for (Message m:messages){
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setConversationId(m.getConversation().getId());
            messageResponse.setContent(m.getContent());
            messageResponse.setAvatarUrl(m.getSender().getAvatarUrl());
            messageResponse.setCreatedAt(m.getCreatedAt());
            messageResponse.setSenderId(m.getSender().getId());
            messageResponseList.add(messageResponse);
        }
        return messageResponseList;
    }

    @Override
    public MessageResponse chat(ChatMessageRequest request,User sender) {
        Long senderId = sender.getId();

        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chat"));
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("Không thể gửi tin nhắn trống");
        }
        if (!senderId.equals(conversation.getUser1().getId())
            && !senderId.equals(conversation.getUser2().getId())) {
            throw new AccessDeniedException("Bạn không thuộc đoạn chat này");
        }
        Message message = new Message();
        User receiver;
        message.setConversation(conversation);
        message.setContent(request.getContent());
        message.setCreatedAt(LocalDateTime.now());
        if (senderId.equals(conversation.getUser1().getId())){
            receiver=  conversation.getUser2();
        }else {
            receiver = conversation.getUser1();
        }
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setSeen(false);
        messageRepository.save(message);
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setId(message.getId());
        messageResponse.setCreatedAt(message.getCreatedAt());
        messageResponse.setConversationId(message.getConversation().getId());
        messageResponse.setContent(message.getContent());
        messageResponse.setSenderId(message.getSender().getId());
        messageResponse.setAvatarUrl(message.getSender().getAvatarUrl());
        return messageResponse;
    }

    @Override
    public long messageNotification(long idReceiver) {
        return messageRepository.countByMessage(idReceiver);
    }

    @Override
    public void seenMessage(int idConversation,long idReceiver) {
        int check = messageRepository.markSeen(idConversation,idReceiver);
        if (check==0){
            throw new RuntimeException("Không có tin nhắn nào cần seen");
        }
    }


}
