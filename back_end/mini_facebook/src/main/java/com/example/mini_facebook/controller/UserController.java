package com.example.mini_facebook.controller;

import com.example.mini_facebook.config.JwtUtil;
import com.example.mini_facebook.dto.*;
import com.example.mini_facebook.exception.UserNotFoundException;
import com.example.mini_facebook.model.Friends;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IUserRepository;
import com.example.mini_facebook.service.HttpServletRequestUtil;
import com.example.mini_facebook.service.ScheduleService;
import com.example.mini_facebook.service.impl.ICloudinaryService;
import com.example.mini_facebook.service.impl.IFriendsService;
import com.example.mini_facebook.service.impl.IPasswordResetTokenService;
import com.example.mini_facebook.service.impl.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;


@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private HttpServletRequestUtil httpServletRequestUtil;
    @Autowired
    private IFriendsService friendsService;
    @Autowired
    private ICloudinaryService cloudinaryService;
    @Autowired
    private ScheduleService scheduleService;

    @PostMapping(value = "/update/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
                                           @RequestPart (value = "bio") String bio,
                                           @RequestPart (value = "fullName") String fullName,
                                           @RequestPart(value = "avatar", required = false) MultipartFile avatar,
                                           @RequestPart(value = "background", required = false) MultipartFile background,
                                           HttpServletRequest httpServletRequest) {
        try {
            String token = httpServletRequestUtil.getToken(httpServletRequest);
            String username = jwtUtil.getUsernameByToken(token);
            UserDTO userDTO = new UserDTO();
            String avatarUrl = null;
            String backgroundUrl = null;
            if (avatar != null && !avatar.isEmpty()) {
                avatarUrl = cloudinaryService.uploadFIle(avatar);
            }
            if (background != null && !background.isEmpty()) {
                backgroundUrl = cloudinaryService.uploadFIle(background);
            }
            userDTO.setBio(bio);
            userDTO.setFullName(fullName);
            userDTO.setBackgroundUrl(backgroundUrl);
            userDTO.setAvatarUrl(avatarUrl);
            userService.updateProfile(username, userDTO);
            return ResponseEntity.ok("Cập nhật profile thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get/user-by-token")
    public ResponseEntity<?> getUserByToken(HttpServletRequest httpServletRequest) {
        try {
            String token = httpServletRequestUtil.getToken(httpServletRequest);
            User user = userService.getUserByToken(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpServletRequest httpServletRequest) {
        try {
            String token = httpServletRequestUtil.getToken(httpServletRequest);
            User user = userService.getUserByToken(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update/password")
    public ResponseEntity<?> suggestFriends(HttpServletRequest httpServletRequest,@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        try {
            String token = httpServletRequestUtil.getToken(httpServletRequest);
            String username = jwtUtil.getUsernameByToken(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng hiện tại."));
            userService.updatePassword(user,updatePasswordDTO.getOldPassword(),updatePasswordDTO.getNewPassword(),updatePasswordDTO.getConfirmPassword());
            return ResponseEntity.ok("Đổi mật khẩu thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add/friend")
    public ResponseEntity<?> addFriend(@RequestBody ReceiverDTO receiverDto, HttpServletRequest request) {
        try {
            String token = httpServletRequestUtil.getToken(request);
            String username = jwtUtil.getUsernameByToken(token);
            User sender = userRepository.findByUsername(username)
                    .orElse(null);
            friendsService.addFriend(sender.getId(), receiverDto.getReceiverId());
            return ResponseEntity.ok("Đã gửi lời mời kết bạn");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Bạn đã gửi lời mời kết bạn này rồi!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/suggest/friends")
    private ResponseEntity<?> friendRequestList(HttpServletRequest request) {
        try {
            String token = httpServletRequestUtil.getToken(request);
            String username = jwtUtil.getUsernameByToken(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng hiện tại."));
            List<UserSuggestionDTO> friendsList = userService.FilterFriendsStatus(user.getUsername());
            return ResponseEntity.ok(friendsList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/accept/add/friend")
    public ResponseEntity<?> acceptAddFriend(@RequestBody SenderDTO senderDto, HttpServletRequest request) {
        try {
            String token = httpServletRequestUtil.getToken(request);
            String username = jwtUtil.getUsernameByToken(token);
            User receiver = userRepository.findByUsername(username)
                    .orElse(null);
            friendsService.acceptAddFriend(senderDto.getSenderId(), receiver.getId());
            return ResponseEntity.ok("Đã kết bạn thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/cancel/add/friend")
    public ResponseEntity<?> cancelAddFriend(@RequestBody ReceiverDTO receiverDto, HttpServletRequest request) {
        try {
            String token = httpServletRequestUtil.getToken(request);
            String username = jwtUtil.getUsernameByToken(token);
            User sender = userRepository.findByUsername(username)
                    .orElse(null);
            long delete = friendsService.cancelAddFriend(sender.getId(), receiverDto.getReceiverId());
            if (delete == 0) {
                return ResponseEntity.badRequest()
                        .body("Không tìm thấy yêu cầu kết bạn đã gửi.");
            }
            return ResponseEntity.ok("Đã hủy gửi lời mời!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refuse/add/friend")
    public ResponseEntity<?> refuseAddFriend(@RequestBody SenderDTO senderDto, HttpServletRequest request) {
        try {
            String token = httpServletRequestUtil.getToken(request);
            String username = jwtUtil.getUsernameByToken(token);
            User receiver = userRepository.findByUsername(username)
                    .orElse(null);
            friendsService.cancelAddFriend(senderDto.getSenderId(),receiver.getId());
            return ResponseEntity.ok("Đã hủy từ chối lời mời!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/friend/invitation")
    public ResponseEntity<?> friendInvitation(HttpServletRequest request) {
        try {
            String token = httpServletRequestUtil.getToken(request);
            String username = jwtUtil.getUsernameByToken(token);
            User receiver = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Không lấy được thông tin người nhận"));
            List<Friends> friends = friendsService.findFriendRequestList(receiver.getId());
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/profile/{id}")
    public ResponseEntity<?> profileUserById(@PathVariable("id") int id){
        try {
            ProFileUserDTO userDTO = userService.findProfileUser(id);
            return ResponseEntity.ok(userDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/getId")
    public ResponseEntity<?>getIdCurrentUser(HttpServletRequest request){
        String token = httpServletRequestUtil.getToken(request);
        String username = jwtUtil.getUsernameByToken(token);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Không lấy được thông tin người dùng"));
        return ResponseEntity.ok(user.getId());
    }

}

