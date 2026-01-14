package com.example.mini_facebook.repository;

import com.example.mini_facebook.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Integer> {
    Optional<PasswordResetToken> findByToken(String token);
}
