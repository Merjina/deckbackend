package com.example.deck.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.deck.model.Review;
import com.example.deck.service.ReviewService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Endpoint to submit a review
    @PostMapping
    public ResponseEntity<Review> submitReview(@RequestBody Review review, HttpServletRequest request) {
        // Extract user information from JWT (for example)
        // Assuming JWT is used, you might extract the username, email, and userId from the token
        // This is where you would validate the token and set the user info, here we are assuming the frontend sends user info
        if (review.getUserId() == null || review.getUsername() == null || review.getEmail() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Review savedReview = reviewService.saveReview(review);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    // Endpoint to get all reviews
    @GetMapping
    public ResponseEntity<List<Review>> getReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}
