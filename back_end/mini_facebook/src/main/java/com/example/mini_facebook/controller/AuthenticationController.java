package com.example.mini_facebook.controller;

import com.example.mini_facebook.config.JwtUtil;
import com.example.mini_facebook.dto.*;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IUserRepository;
import com.example.mini_facebook.service.OtpService;
import com.example.mini_facebook.service.impl.IPasswordResetTokenService;
import com.example.mini_facebook.service.impl.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private OtpService otpService;
    @Autowired
    private IPasswordResetTokenService passwordResetTokenService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody OtpDTO otpDto){
        try {
            if (userRepository.findByUsername(otpDto.getUsername()).isPresent()){
                return ResponseEntity.badRequest().body("Username đã được sử dụng!");
            }
            boolean check = otpService.validateOtp(otpDto.getEmail(), otpDto.getCode());
            if (!check) {
                return ResponseEntity.badRequest().body("OTP không đúng!");
            }
            userService.register(
                    otpDto.getFullName(),
                    otpDto.getUsername(),
                    otpDto.getPassword(),
                    otpDto.getEmail());
            otpService.deleteOtpByEmail(otpDto.getEmail());
            return ResponseEntity.ok("Đăng ký thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/get/otp")
    public ResponseEntity<?> getOtp(@RequestBody EmailDTO email){
        try {
            User user = userRepository.findUserByEmail(email.getEmail()).orElse(null);
            if (user!=null) {
                return ResponseEntity.badRequest().body("Email đã được sử dụng!");
            }
            String otp = OtpUtil.randomOtp();
            otpService.saveOtp(email.getEmail(),otp);
            return ResponseEntity.ok("Đã gửi OTP về email");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            ResponseToken responseToken = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(responseToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request){
        try {
            ResponseToken responseToken = userService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(responseToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/forgot/password")
    public ResponseEntity<?> updateAvatar(@RequestBody PasswordResetTokenDTO email) {
        try {
            if (email.getEmail() == null || email.getEmail().trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("Email không được để trống");
            }
            passwordResetTokenService.createResetPasswordToken(email.getEmail());
            return ResponseEntity.ok("Vui lòng kiểm tra email để đặt lại mật khẩu");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @PostMapping("/forgot/password/confirm")
    public ResponseEntity<?> updateAvatar(@RequestBody ForgotPasswordDTO password) {
        try {
            passwordResetTokenService.forgotPassword(password.getToken(),password.getPassword(),password.getConfirmPassword());
            return ResponseEntity.ok("Đã đổi mật khẩu thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
