package com.example.mini_facebook.dto;

import com.example.mini_facebook.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProFileUserDTO {
    private int id;
    private String fullName;
    private String bio;
    private String avatar;
    private String background;
    private List<PostDTO> posts;
}
