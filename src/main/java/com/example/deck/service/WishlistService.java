package com.example.deck.service;

import com.example.deck.model.WishList;
import com.example.deck.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    // Get all wishlist items for a specific user
    public List<WishList> getWishlistItemsByUser(String username) {
        return wishlistRepository.findByUsername(username); // Fetch wishlist items based on username
    }

    // Add item to wishlist (Prevent Duplicates for a specific user)
    public WishList addToWishlist(WishList wishlistItem) {
        // Check if the product already exists in the user's wishlist
        WishList existingItem = wishlistRepository.findByProductIdAndUsername(wishlistItem.getProductId(), wishlistItem.getUsername());
        if (existingItem == null) {
            return wishlistRepository.save(wishlistItem); // Save if no duplicate
        }
        return null; // Item already exists
    }

    // Remove item from wishlist for a specific user
    public boolean removeFromWishlist(Long productId, String username) {
        // Find item by productId and username
        WishList item = wishlistRepository.findByProductIdAndUsername(productId, username);
        if (item != null) {
            wishlistRepository.delete(item); // Delete the item if found
            return true;
        }
        return false; // Item not found
    }
}
