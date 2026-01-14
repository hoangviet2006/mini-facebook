package com.example.mini_facebook.service;

import com.example.mini_facebook.dto.NotificationDTO;
import com.example.mini_facebook.exception.UserNotFoundException;
import com.example.mini_facebook.model.Friends;
import com.example.mini_facebook.model.Notifications;
import com.example.mini_facebook.model.Post;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IFriendsRepository;
import com.example.mini_facebook.repository.INotificationRepository;
import com.example.mini_facebook.repository.IUserRepository;
import com.example.mini_facebook.service.impl.IFriendsService;
import com.example.mini_facebook.service.impl.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService implements INotificationService {
    @Autowired
    private IFriendsService friendsService;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private INotificationRepository notificationRepository;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void notificationNewPost(Post post) {
            if (post == null) return;
            List<Friends> friends = friendsService.findAllFriendByCurrentId(post.getUser().getId(), post.getUser().getId());
            if (friends != null) {
                for (Friends friend : friends) {
                    Notifications notification = new Notifications();
                    notification.setType("NEW_POST");
                    notification.setSender(post.getUser());
                    User receiver;
                    if (friend.getSender().getId().equals(post.getUser().getId())) {
                        receiver = friend.getReceiver();
                    } else {
                        receiver = friend.getSender();
                    }
                    receiver = userRepository.findUserById(receiver.getId()).orElseThrow(() -> new UserNotFoundException("không tìm thấy thông tin người dùng"));
                    notification.setReceiver(receiver);
                    notification.setPostId(post.getId());
                    notification.setRead(false);
                    notification.setCreatedAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                    NotificationDTO dto = new NotificationDTO(
                            notification.getId(),
                            "NEW_POST",
                            notification.getSender().getFullName() + " vừa đăng bài viết mới",
                            notification.getPostId(),
                            false,
                            notification.getCreatedAt()
                    );
                    simpMessagingTemplate.convertAndSend("/topic/notification/" + receiver.getId(), dto);
                }
            }
        }
}
