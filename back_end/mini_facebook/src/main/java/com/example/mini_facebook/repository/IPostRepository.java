package com.example.mini_facebook.repository;

import com.example.mini_facebook.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post,Integer> {
    @Query(value = "select * from post p where p.soft_delete=false and p.user_id = ?1 order by p.created_at desc",nativeQuery = true)
    List<Post> findPostByUserId(Long userId);
    @Query(value = "select * from post p where p.soft_delete=false order by p.created_at desc",nativeQuery = true)
    List<Post> findAll();

    Post findPostsById(Long id);
}
