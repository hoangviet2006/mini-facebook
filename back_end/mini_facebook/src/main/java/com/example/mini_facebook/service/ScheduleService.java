package com.example.mini_facebook.service;

import com.example.mini_facebook.model.PasswordResetToken;
import com.example.mini_facebook.repository.IPasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {
    @Autowired
    private IPasswordResetTokenRepository passwordResetToken;
    @Scheduled(fixedRate = 60000) // chạy mỗi 1 phút
    public void cleanExpiredEmails() {
        List<PasswordResetToken> passwordResetTokenList = passwordResetToken.findAll().stream()
                .filter(e->e.isUsed()||e.getExpiryDate().isBefore(LocalDateTime.now())).toList();
        if (!passwordResetTokenList.isEmpty()){
            passwordResetToken.deleteAll(passwordResetTokenList);
            System.out.println("Đã dọn dẹp được : "+ passwordResetTokenList.size() + " bản ghi");
        }
    }
}
