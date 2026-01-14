package com.example.mini_facebook.service;

import com.example.mini_facebook.config.JwtUtil;
import com.example.mini_facebook.exception.UnauthorizedException;
import com.example.mini_facebook.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class HttpServletRequestUtil {
    public String getToken(HttpServletRequest request){
        String auth = request.getHeader("Authorization");
        if (auth==null||!auth.startsWith("Bearer ")){
            throw new UnauthorizedException("Không lấy được thông tin người dùng!");
        }
        return auth.substring(7);
    }

}
