package com.example.mini_facebook.service;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SendEmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    public void sendMailOTP(String email,String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); // người nhận
        message.setSubject("Mã OTP Xác Thực Đăng Ký Tài Khoản!"); // tiêu đề
        message.setText("Mã OTP của bạn là: "+otp +".\n" +
                        "Vui lòng không được chia sẽ cho bất kì ai để đảm bảo an toàn cho tài khoản của bạn");
        javaMailSender.send(message);
    }
    public void sendEmailByForgotPassword(String email,String token){

        String resetLink ="http://localhost:3000/forgot-password-confirm?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Lấy lại mật khẩu");
        message.setText(
                "Nhấn vào link bên dưới để đặt lại mật khẩu:\n" +
                resetLink + "\n\n" +
                "Link có hiệu lực trong 15 phút.\n" +
                "Vui lòng không chia sẻ link này cho bất kỳ ai."
        );
        javaMailSender.send(message);
    }
}
