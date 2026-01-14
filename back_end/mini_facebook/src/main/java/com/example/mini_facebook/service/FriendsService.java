package com.example.mini_facebook.service;

import com.example.mini_facebook.exception.UserNotFoundException;
import com.example.mini_facebook.model.Friends;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IFriendsRepository;
import com.example.mini_facebook.repository.IUserRepository;
import com.example.mini_facebook.service.impl.IFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendsService implements IFriendsService {
    @Autowired
    private IFriendsRepository friendsRepository;
    @Autowired
    private IUserRepository userRepository;
    @Override
    public void addFriend(long senderId,long receiverId) {
        User sender = userRepository.findUserById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người gửi yêu cầu kết bạn."));
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(()->new UserNotFoundException("Không tìm thấy người nhận yêu cầu kết bạn."));
        if(sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("Bạn không thể tự gửi lời mời kết bạn cho chính mình.");
        }
        Friends friend = new Friends();
        friend.setSender(sender);
        friend.setReceiver(receiver);
        friend.setStatus(Friends.Status.PENDING);
        friendsRepository.save(friend);
    }

    @Override
    public long cancelAddFriend(long senderId, long receiverId) {
        User sender = userRepository.findUserById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người gửi yêu cầu kết bạn."));
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(()->new UserNotFoundException("Không tìm thấy người nhận yêu cầu kết bạn."));
        Friends friends = friendsRepository.findBySender_IdAndReceiver_Id(sender.getId(),receiver.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy lời mời kết bạn hoặc lời mời đã bị hủy trước đó."
                ));
        if (friends.getStatus() != Friends.Status.PENDING) {
            return 0;
        }
        return friendsRepository.deleteBySender_IdAndReceiver_Id(sender.getId(), receiver.getId());
    }

    @Override
    public List<Friends> findFriendRequestList(Long receiverId) {
        return friendsRepository.findFriendRequestList(receiverId);
    }

    @Override
    public boolean acceptAddFriend(long senderId, long receiverId) {
        User sender = userRepository.findUserById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người gửi yêu cầu kết bạn."));
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(()->new UserNotFoundException("Không tìm thấy người nhận yêu cầu kết bạn."));
        if(sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("không thể kết bạn với chính mình");
        }
        Friends friends = friendsRepository.findBySender_IdAndReceiver_Id(sender.getId(),receiver.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy lời mời kết bạn hoặc lời mời đã bị hủy."
                ));
        if (friends.getStatus() == Friends.Status.APPROVED) {
            throw new RuntimeException("Bạn đã là bạn bè với người này trước đó.");
        }
        friends.setStatus(Friends.Status.APPROVED);
        friendsRepository.save(friends);
        return true;
    }
    @Override
    public List<Friends> findAllFriendByCurrentId(Long idSender,Long idReceiver) {
        return friendsRepository.findALlFriendByCurrentUser(idSender,idReceiver);
    }
}
