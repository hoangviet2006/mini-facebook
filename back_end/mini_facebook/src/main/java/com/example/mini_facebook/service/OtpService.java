    package com.example.mini_facebook.service;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.stereotype.Service;

    import java.util.concurrent.TimeUnit;

    @Service
    public class OtpService {
        @Autowired
        private  RedisTemplate<String,String> redisTemplate;
        @Autowired
        private SendEmailService sendEmailService;
        private static final String OTP_PREFIX = "OTP:";
        public void saveOtp(String email,String otp){
            String key = OTP_PREFIX+email;
            redisTemplate.opsForValue().set(key,otp,5, TimeUnit.MINUTES);
            sendEmailService.sendMailOTP(email,otp);
        }
        public String getOtp(String email){
            return redisTemplate.opsForValue().get(OTP_PREFIX+email);
        }
        public void deleteOtpByEmail(String email) {
            redisTemplate.delete(OTP_PREFIX+ email);
        }
        public boolean validateOtp(String email,String otp){
            String otpRedis = getOtp(email);
            if (otpRedis == null) {
                throw new RuntimeException("OTP đã hết hạn hoặc không tồn tại");
            }
            if (!otpRedis.equals(otp)) {
                return false;
            }
            deleteOtpByEmail(email);
            return true;
        }
    }
