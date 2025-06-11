package com.example.deck.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String productId; // Changed from Long to String

    @Column(length = 1000)
    private String description;

    private double price;
    private String imagePath;

    private String dimension;

    @ElementCollection
    private List<String> availableColors;

    @ElementCollection
    private List<String> availableMaterials;

    @ElementCollection
    private List<String> sizeOptions;

    private boolean allowCustomNotes;
    private double customizationBaseCost;

    // No-arg constructor (required by JPA)
    public Product() {}

    // All-arg constructor
    public Product(Long id, String name, String productId, String description, double price, String imagePath,
                   String dimension, List<String> availableColors, List<String> availableMaterials,
                   List<String> sizeOptions, boolean allowCustomNotes, double customizationBaseCost) {
        this.id = id;
        this.name = name;
        this.productId = productId;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
        this.dimension = dimension;
        this.availableColors = availableColors;
        this.availableMaterials = availableMaterials;
        this.sizeOptions = sizeOptions;
        this.allowCustomNotes = allowCustomNotes;
        this.customizationBaseCost = customizationBaseCost;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }

    public List<String> getAvailableColors() { return availableColors; }
    public void setAvailableColors(List<String> availableColors) { this.availableColors = availableColors; }

    public List<String> getAvailableMaterials() { return availableMaterials; }
    public void setAvailableMaterials(List<String> availableMaterials) { this.availableMaterials = availableMaterials; }

    public List<String> getSizeOptions() { return sizeOptions; }
    public void setSizeOptions(List<String> sizeOptions) { this.sizeOptions = sizeOptions; }

    public boolean isAllowCustomNotes() { return allowCustomNotes; }
    public void setAllowCustomNotes(boolean allowCustomNotes) { this.allowCustomNotes = allowCustomNotes; }

    public double getCustomizationBaseCost() { return customizationBaseCost; }
    public void setCustomizationBaseCost(double customizationBaseCost) { this.customizationBaseCost = customizationBaseCost; }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", productId='" + productId + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imagePath='" + imagePath + '\'' +
                ", dimension='" + dimension + '\'' +
                ", availableColors=" + availableColors +
                ", availableMaterials=" + availableMaterials +
                ", sizeOptions=" + sizeOptions +
                ", allowCustomNotes=" + allowCustomNotes +
                ", customizationBaseCost=" + customizationBaseCost +
                '}';
    }
}
