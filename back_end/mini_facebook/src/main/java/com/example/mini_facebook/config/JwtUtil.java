package com.example.mini_facebook.config;

import com.example.mini_facebook.exception.UserNotFoundException;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IUserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Autowired
    private IUserRepository userRepository;
    String secret = "my_super_secret_key_that_is_long_enough_32_bytes";
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username){
       User user = userRepository.findByUsername(username).orElse(null);
       if (user==null){
           throw new UserNotFoundException("User không tồn tại trong hệ thống");
       }
       return Jwts.builder()
               .setSubject(username) // thông tin chính
               .claim("email",user.getEmail()) // thông tin phụ
               .claim("role", "ROLE_" + user.getRole())
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis()+60* 60 * 1000))
               .signWith(key)
               .compact();
    }
    public String generateRefreshToken(String username){
        User user = userRepository.findByUsername(username).orElse(null);
        if (user==null){
            throw new UserNotFoundException("User không tồn tại trong hệ thống");
        }
        return Jwts.builder()
                .setSubject(username)
                .claim("email",user.getEmail())
                .claim("role",user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10L*24* 60 * 60* 1000)) // 10 ngày
                .signWith(key)
                .compact();
    }
    public String getUsernameByToken(String token){
        return Jwts.parser()// đọc token
                .verifyWith(key) // xác minh chữ ký
                .build()
                .parseSignedClaims(token)// xác minh xem chữ ký có hợp lệ hay không
                .getPayload() // giải mã payload để lấy thông tin khi cài token
                .getSubject(); // lấy phần subject
    }
    public String getEmailByToken(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email",String.class);
    }
    public String getRoleByToken(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role",String.class);
    }
    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token); // xác minh xem chữ ký có hợp lệ hay không
            return true;
        } catch (Exception e) {
            return  false;
        }
    }

}
