package com.example.mini_facebook.repository;

import com.example.mini_facebook.model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface INotificationRepository extends   JpaRepository<Notifications,Integer> {
}
