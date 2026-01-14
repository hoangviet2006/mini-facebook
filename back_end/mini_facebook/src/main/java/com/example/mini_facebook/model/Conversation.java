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
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
)
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_1",nullable = false)
    private User user1;
    @ManyToOne
    @JoinColumn(name = "user_2",nullable = false)
    private User user2;
    @Column(name = "create_at")
    private LocalDateTime createdAt;
}
