package com.example.deck.service;

import org.springframework.stereotype.Service;
import com.example.deck.model.Product;
import com.example.deck.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Save or update product
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Fetch all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // Check if product exists by ID
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    // Get product by productId (String)
    public Optional<Product> getProductByProductId(String productId) {
        return productRepository.findByProductId(productId); // No conversion needed, pass the String
    }

    // Check if product exists by productId (String)
    public boolean existsByProductId(String productId) {
        return productRepository.existsByProductId(productId); // No conversion needed, pass the String
    }

    // Delete product by ID
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    public List<Product> searchProducts(String query) {
        if (query == null || query.isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }
}
