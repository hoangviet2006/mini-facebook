package com.example.mini_facebook.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String backgroundUrl;
    private String bio;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
    public enum Role{
        USER,
        ADMIN
    }
}
