package com.example.deck.controller;

import com.example.deck.model.WishList;
import com.example.deck.security.JwtUtil;
import com.example.deck.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000","https://deck-7kf5-merjinas-projects.vercel.app"}) // Allow your React app to make requests
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private JwtUtil jwtUtil;  // Inject JwtUtil to access token-related functionality

    // Get all wishlist items for a specific user
    @GetMapping
    public List<WishList> getWishlistItems(HttpServletRequest request) {
        // Extract the username (email) from the JWT token
        String token = jwtUtil.getTokenFromRequest(request);
        if (token == null || jwtUtil.isTokenExpired(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String username = jwtUtil.extractUsername(token);  // Get the username from the token

        // Return wishlist items filtered by user (username)
        return wishlistService.getWishlistItemsByUser(username);
    }

    // Add an item to the wishlist
    @PostMapping("/add")
    public ResponseEntity<String> addToWishlist(@RequestBody WishList wishlistItem, HttpServletRequest request) {
        // Extract the token from request
        String token = jwtUtil.getTokenFromRequest(request);
        if (token == null || jwtUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired token");
        }
        
        // Extract the username from the token
        String username = jwtUtil.extractUsername(token);  
        wishlistItem.setUsername(username); // Set username in wishlist item
        
        // Add the item to wishlist using service
        WishList addedItem = wishlistService.addToWishlist(wishlistItem);
        if (addedItem != null) {
            return ResponseEntity.ok("Item added to wishlist!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Item already in wishlist!");
        }
    }

    // Remove an item from the wishlist by productId
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromWishlist(@PathVariable Long productId, HttpServletRequest request) {
        // Extract the token from request
        String token = jwtUtil.getTokenFromRequest(request);
        if (token == null || jwtUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired token");
        }
        
        // Extract the username from the token
        String username = jwtUtil.extractUsername(token);

        // Remove the item from wishlist using service
        boolean removed = wishlistService.removeFromWishlist(productId, username);
        if (removed) {
            return ResponseEntity.ok("Item removed from wishlist");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wishlist item not found");
        }
    }
}
