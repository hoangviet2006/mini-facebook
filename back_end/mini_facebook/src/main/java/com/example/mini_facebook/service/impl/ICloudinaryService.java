package com.example.mini_facebook.service.impl;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ICloudinaryService {
    String uploadFIle(MultipartFile file) throws IOException;
}
