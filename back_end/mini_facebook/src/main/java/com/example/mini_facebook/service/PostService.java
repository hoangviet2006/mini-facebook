package com.example.mini_facebook.service;

import com.example.mini_facebook.model.Post;
import com.example.mini_facebook.repository.IPostRepository;
import com.example.mini_facebook.service.impl.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService implements IPostService {
    @Autowired
    private IPostRepository postRepository;
    @Override
    public List<Post> findPost() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> findPostByUser(Long userId) {
        return postRepository.findPostByUserId(userId);
    }

    @Override
    public boolean deletePost(Long idPost) {
        Post post = postRepository.findPostsById(idPost);
        if (post==null) throw new RuntimeException("Không tìm thấy bài viết");
        post.setSoftDelete(true);
        postRepository.save(post);
        return true;
    }
}
