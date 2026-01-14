package com.example.mini_facebook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSuggestionDTO {
    private Long id;
    private String fullName;
    private String avatarUrl;
    private String action;
}
