package com.example.mini_facebook.service;

import com.example.mini_facebook.exception.EmailNotFoundException;
import com.example.mini_facebook.exception.UserNotFoundException;
import com.example.mini_facebook.model.PasswordResetToken;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IPasswordResetTokenRepository;
import com.example.mini_facebook.repository.IUserRepository;
import com.example.mini_facebook.service.impl.IPasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Encoder;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class PasswordResetTokenService implements IPasswordResetTokenService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IPasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private SendEmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createResetPasswordToken(String email) {
        email = email.trim();
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new EmailNotFoundException("Email bạn nhập không hợp lệ! Vui lòng nhập lại"));
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setUsed(false);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        passwordResetToken.setToken(token);
        passwordResetTokenRepository.save(passwordResetToken);
        emailService.sendEmailByForgotPassword(email, token);
    }

    @Override
    public void forgotPassword(String token, String password,String confirmPassword) {
        token = token == null ? "" : token.trim();
        password = password == null ? "" : password.trim();
        if (token.isEmpty()) {
            throw new RuntimeException("Token không được để trống");
        }
        if (password.isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token.trim())
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));
        if (passwordResetToken.isUsed()) {
            throw new RuntimeException("Token đã được sử dụng");
        }
        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token đã hết hạn");
        }
        if (!password.equals(confirmPassword)){
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(password.trim()));
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
        userRepository.save(user);

    }
}
