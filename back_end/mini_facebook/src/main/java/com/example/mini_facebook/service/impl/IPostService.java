package com.example.mini_facebook.service.impl;

import com.example.mini_facebook.model.Post;

import java.util.List;

public interface IPostService {
    List<Post> findPost();
    List<Post> findPostByUser(Long userId);
    boolean deletePost(Long idPost);
}
