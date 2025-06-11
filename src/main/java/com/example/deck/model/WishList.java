package com.example.deck.model;

import jakarta.persistence.*;

@Entity
@Table(name = "wishlist")
public class WishList {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long productId;
    private String name;
    private String imagePath;
    private Double price;
    private String username;

    // Constructors
    public WishList() {}

    public WishList(Long productId, String name, String imagePath, Double price,String username) {
        this.productId = productId;
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
        this.username=username;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
