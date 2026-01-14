package com.example.mini_facebook.dto;

import java.util.Random;

public class OtpUtil {
    public static String randomOtp(){
        Random random = new Random();
        int otp= 100000+random.nextInt(900000);
        return String.valueOf(otp);
    }
}
