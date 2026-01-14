package com.example.mini_facebook.controller;
import com.example.mini_facebook.dto.ChatMessageRequest;
import com.example.mini_facebook.dto.MessageResponse;
import com.example.mini_facebook.exception.UserNotFoundException;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IMessageRepository;
import com.example.mini_facebook.repository.IUserRepository;
import com.example.mini_facebook.service.impl.IConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class ChatSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private IConversationService conversationService;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IMessageRepository messageRepository;
    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest chatMessageRequest){
        User sender = userRepository.findUserById(chatMessageRequest.getIdSender()).orElseThrow(() -> new UserNotFoundException("Không lấy được thông tin người dùng"));
        MessageResponse response = conversationService.chat(chatMessageRequest, sender);
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + response.getConversationId(),
                response
        );
    }
}
