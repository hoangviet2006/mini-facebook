package com.example.mini_facebook.repository;

import com.example.mini_facebook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findUserByEmail(String email);
    @Query(value = "select * from user u where u.username !=?1 ",nativeQuery = true)
    List<User> findUserAll(String username);
    Optional<User> findUserById(long id);


}
