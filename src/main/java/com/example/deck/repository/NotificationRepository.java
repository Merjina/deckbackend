package com.example.deck.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deck.model.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserEmail(String userEmail);
}
