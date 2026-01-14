package com.example.mini_facebook.controller;

import com.example.mini_facebook.config.JwtUtil;
import com.example.mini_facebook.dto.*;
import com.example.mini_facebook.exception.UserNotFoundException;
import com.example.mini_facebook.model.Conversation;
import com.example.mini_facebook.model.Message;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IUserRepository;
import com.example.mini_facebook.service.HttpServletRequestUtil;
import com.example.mini_facebook.service.impl.IConversationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-message")
public class ChatMessageController {
    @Autowired
    private IConversationService conversationService;
    @Autowired
    private HttpServletRequestUtil httpServletRequestUtil;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/create/conversation")
    public ResponseEntity<?> createConversation(HttpServletRequest request, @RequestBody CreateConversationRequest conversationRequest) {
        try {
            String token = httpServletRequestUtil.getToken(request);
            String username = jwtUtil.getUsernameByToken(token);
            User currentUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Không lấy được thông tin người dùng"));
            User user = userRepository.findUserById(conversationRequest.getUserId()).orElseThrow(() -> new UserNotFoundException("Không lấy được thông tin người dùng còn lại"));
            if (currentUser.getId().equals(conversationRequest.getUserId())) {
                throw new RuntimeException("Không thể tự nhắn tin với chính mình");
            }
            Conversation conversation = conversationService.getOrCreateConversation(currentUser, user);
            ConversationResponse conversationResponse = new ConversationResponse();
            conversationResponse.setIdConversation(conversation.getId());
            conversationResponse.setIdUser(user.getId());
            conversationResponse.setAvatar(user.getAvatarUrl());
            conversationResponse.setFullName(user.getFullName());
            return ResponseEntity.ok(conversationResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<?> getMessage(HttpServletRequest request, @PathVariable int id) {
        try {
            String token = httpServletRequestUtil.getToken(request);
            String username = jwtUtil.getUsernameByToken(token);
            User currentUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Không lấy được thông tin người dùng"));
            List<MessageResponse> messages = conversationService.getMessage(id, currentUser);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/conversations/inbox")
    public ResponseEntity<?> conversationInbox(HttpServletRequest request) {
        try {
            String token = httpServletRequestUtil.getToken(request);
            String username = jwtUtil.getUsernameByToken(token);
            User currentUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Không lấy được thông tin người dùng"));
            List<ConversationResponse> conversations = conversationService.getInbox(currentUser);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/seen")
    public ResponseEntity<?> seen(HttpServletRequest request,@RequestBody SeenMessageRequest seenMessageRequest){
        try {
            String token = httpServletRequestUtil.getToken(request);
            String username = jwtUtil.getUsernameByToken(token);
            User currentUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Không lấy được thông tin người dùng"));
            conversationService.seenMessage(seenMessageRequest.getIdConversation(),currentUser.getId());
            return ResponseEntity.ok("Đã đọc tin nhắn");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
