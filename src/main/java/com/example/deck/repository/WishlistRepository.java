package com.example.deck.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.deck.model.WishList;

public interface WishlistRepository extends JpaRepository<WishList, Long> {

    // ✅ Find wishlist item for a user-product combo
    WishList findByProductIdAndUsername(Long productId, String username);

    // ✅ List all items for a specific user
    List<WishList> findByUsername(String username);

    // ✅ Remove wishlist item
    @Transactional
    void deleteByProductIdAndUsername(Long productId, String username);
}
