package com.example.deck.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import com.example.deck.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByProductId(Long productId);

    @Transactional
    void deleteByProductId(Long productId);
    
    
    Cart findByProductIdAndUsername(Long productId, String username);

    // ✅ Get all items for a specific user
    List<Cart> findByUsername(String username);

    // ✅ Remove item from user's cart
    @Transactional
    void deleteByProductIdAndUsername(Long productId, String username);

}
