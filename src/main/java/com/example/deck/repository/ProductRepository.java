package com.example.deck.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.deck.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Fetch product by productId (String)
    Optional<Product> findByProductId(String productId);

    // Check if product exists by productId (String)
    boolean existsByProductId(String productId);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

}
