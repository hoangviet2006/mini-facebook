package com.example.mini_facebook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponse {
    private long idConversation;
    private long idUser;
    private String fullName;
    private String avatar;
}
