package com.example.mini_facebook.service;

import com.example.mini_facebook.config.JwtUtil;
import com.example.mini_facebook.dto.*;
import com.example.mini_facebook.exception.UserNotFoundException;
import com.example.mini_facebook.model.Friends;
import com.example.mini_facebook.model.Post;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IFriendsRepository;
import com.example.mini_facebook.repository.IPostRepository;
import com.example.mini_facebook.repository.IUserRepository;
import com.example.mini_facebook.service.impl.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private IFriendsRepository friendsRepository;

    @Override
    public User register(String fullName, String username, String password, String email) {
        User user = new User();
        user.setFullName(fullName);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(User.Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        System.out.println("ROLE BEFORE SAVE = " + user.getRole());
        userRepository.save(user);
        return user;
    }

    @Override
    public ResponseToken login(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new UserNotFoundException("Username không tồn tại trong hệ thống! Vui lòng nhập lại");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng! Vui lòng nhập lại");
        }
        ResponseToken responseToken = new ResponseToken();
        responseToken.setAccessToken(jwtUtil.generateToken(username));
        responseToken.setRefreshToken(jwtUtil.generateRefreshToken(username));
        return responseToken;
    }

    @Override
    public boolean updateProfile(String username, UserDTO user) {
        User findUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User không tồn tại"));
        if (user.getAvatarUrl() != null) {
            findUser.setAvatarUrl(user.getAvatarUrl());
        }
        if (user.getBackgroundUrl() != null) {
            findUser.setBackgroundUrl(user.getBackgroundUrl());
        }
        if (user.getBio() != null) {
            findUser.setBio(user.getBio());
        }
        if (user.getFullName() != null) {
            findUser.setFullName(user.getFullName());
        }
        userRepository.save(findUser);
        return true;
    }

    @Override
    public Post createPost(PostDTO postDto, User user) {
        Post post = new Post();
        post.setCreatedAt(LocalDateTime.now());
        post.setContent(postDto.getContent());
        post.setUrl(postDto.getUrl());
        post.setSoftDelete(false);
        if (postDto.getUrl() != null) {
            if (postDto.getUrl().endsWith(".mp4")) {
                post.setMediaType(Post.MediaType.VIDEO);
            } else {
                post.setMediaType(Post.MediaType.IMAGE);
            }
        } else {
            post.setMediaType(null);
        }
        post.setUser(user);
        return postRepository.save(post);
    }

    @Override
    public User getUserByToken(String token) {
        String username = jwtUtil.getUsernameByToken(token);
        return userRepository.findByUsername(username).
                orElseThrow(() -> new UserNotFoundException("Không tìm thấy thông tin người dùng!"));
    }

    @Override
    public void updatePassword(User user, String oldPassword, String newPassword, String confirmPassword) {
        if (user == null) {
            throw new UserNotFoundException("User không tồn tại");
        }
        oldPassword = oldPassword == null ? "" : oldPassword.trim();
        newPassword = newPassword == null ? "" : newPassword.trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<User> findUserAll(String username) {
        return userRepository.findUserAll(username);
    }

    @Override
    public List<UserSuggestionDTO> FilterFriendsStatus(String currentUser) {
        User user = userRepository.findByUsername(currentUser)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng hiện tại"));
        List<User> userList = userRepository.findUserAll(user.getUsername());
        List<Friends> friends = friendsRepository.findBySender_IdOrReceiver_Id(user.getId(), user.getId());
        List<UserSuggestionDTO> suggestions = new ArrayList<>();
        for (User u : userList) {
            String action = "add";
            Friends fr = friends.stream()
                    .filter(f -> (
                            f.getSender().getId().equals(user.getId()) && f.getReceiver().getId().equals(u.getId())
                            ||
                            f.getReceiver().getId().equals(user.getId()) && f.getSender().getId().equals(u.getId())))
                    .findFirst().orElse(null);
            if (fr != null) {
                if (fr.getStatus() == Friends.Status.APPROVED) {
                    continue;
                } else if (fr.getSender().getId().equals(user.getId()) && fr.getStatus() == Friends.Status.PENDING) {
                    action = "cancel";
                } else if (fr.getReceiver().getId().equals(user.getId()) && fr.getStatus() == Friends.Status.PENDING) {
                    continue;
                }
            }
            suggestions.add(new UserSuggestionDTO(u.getId(), u.getFullName(), u.getAvatarUrl(), action));
        }
        return suggestions;
    }

    @Override
    public ProFileUserDTO findProfileUser(int userId) {
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("không tìm thấy thông tin người dùng"));
        List<Post> posts = postRepository.findPostByUserId(user.getId());
        ProFileUserDTO proFileUserDTO = new ProFileUserDTO();
        List<PostDTO> postDTOList = new ArrayList<>();
        for (Post p : posts) {
            PostDTO postDTO = new PostDTO();
            postDTO.setContent(p.getContent());
            postDTO.setUrl(p.getUrl());
            postDTO.setCreateAt(p.getCreatedAt());
            postDTOList.add(postDTO);
        }
        proFileUserDTO.setPosts(postDTOList);
        proFileUserDTO.setFullName(user.getFullName());
        proFileUserDTO.setBio(user.getBio());
        proFileUserDTO.setAvatar(user.getAvatarUrl());
        proFileUserDTO.setBackground(user.getBackgroundUrl());
        return proFileUserDTO;
    }

    @Override
    public ResponseToken refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)){
            throw new RuntimeException("Refresh token không hợp lệ");
        }
        String username = jwtUtil.getUsernameByToken(refreshToken);
        String newAccess = jwtUtil.generateToken(username);
        String newRefresh = jwtUtil.generateRefreshToken(username);
        return new ResponseToken(newAccess,newRefresh);
    }


}
