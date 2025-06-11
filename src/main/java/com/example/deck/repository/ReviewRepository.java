package com.example.deck.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deck.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // You can add custom queries if necessary
}
