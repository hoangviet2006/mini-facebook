package com.example.mini_facebook.service.impl;

public interface IPasswordResetTokenService {
    void createResetPasswordToken(String email);
    void forgotPassword(String token,String password,String confirmPassword) ;
}
