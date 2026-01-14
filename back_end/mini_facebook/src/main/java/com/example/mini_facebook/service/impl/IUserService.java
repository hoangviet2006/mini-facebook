package com.example.mini_facebook.service.impl;

import com.example.mini_facebook.dto.*;
import com.example.mini_facebook.model.Friends;
import com.example.mini_facebook.model.Post;
import com.example.mini_facebook.model.User;

import java.util.List;

public interface IUserService {
    User register(String fullName,String username,String password,String email);
    ResponseToken login(String username, String password);
    boolean updateProfile(String username, UserDTO user);
    Post createPost(PostDTO postDto, User user);
    User getUserByToken(String token);
    void updatePassword(User user,String oldPassword,String newPassword,String confirmPassword);
    List<User> findUserAll(String username);
    List<UserSuggestionDTO> FilterFriendsStatus(String currentUser);
    ProFileUserDTO findProfileUser(int userId);
    ResponseToken refreshToken(String refreshToken);
}
