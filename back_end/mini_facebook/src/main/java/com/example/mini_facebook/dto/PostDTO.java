package com.example.mini_facebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class PostDTO {
    private String content;
    private String url;
    private LocalDateTime createAt;
}
