package com.example.mini_facebook.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "notifications")
    public class Notifications {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String type; // NEW_POST, MESSAGE

        @ManyToOne
        @JoinColumn(name = "sender_id")
        private User sender;

        @ManyToOne
        @JoinColumn(name = "receiver_id")
        private User receiver;

        private Long postId;

        private boolean read;

        private LocalDateTime createdAt;
}
