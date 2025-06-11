package com.example.deck.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deck.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByResetToken(String resetToken);
    List<User> findByRole(String role);
}
