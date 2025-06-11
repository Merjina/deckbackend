package com.example.deck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.deck.model.Review;
import com.example.deck.repository.ReviewRepository;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // Method to save a review
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    // Method to get all reviews
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
