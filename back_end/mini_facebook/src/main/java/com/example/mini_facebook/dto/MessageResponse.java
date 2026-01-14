package com.example.mini_facebook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.print.attribute.standard.Sides;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long id;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
    private int conversationId;
    private String avatarUrl;
}
