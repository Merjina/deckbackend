package com.example.deck.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.example.deck.model.Product;
import com.example.deck.service.ProductService;

import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:3000","https://deck-7kf5-merjinas-projects.vercel.app"}) // Allow your React app to make requests

public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public Product addProduct(@RequestParam("name") String name,
                              @RequestParam("productId") String productId,
                              @RequestParam("description") String description,
                              @RequestParam("price") double price,
                              @RequestParam("dimension") String dimension,
                              @RequestParam(value = "availableColors", required = false) List<String> availableColors,
                              @RequestParam(value = "availableMaterials", required = false) List<String> availableMaterials,
                              @RequestParam(value = "sizeOptions", required = false) List<String> sizeOptions,
                              @RequestParam("image") MultipartFile image) {

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        String uploadDir = "uploads/";
        Path path = Paths.get(uploadDir + fileName);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, image.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file.", e);
        }

        Product product = new Product();
        product.setName(name);
        product.setProductId(productId);
        product.setDescription(description);
        product.setPrice(price);
        product.setImagePath(fileName);
        product.setDimension(dimension);
        product.setAvailableColors(availableColors);
        product.setAvailableMaterials(availableMaterials);
        product.setSizeOptions(sizeOptions);

        return productService.saveProduct(product);
    }

    @GetMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get("uploads/").resolve(filename);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("productId") String productId,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("price") double price,
                                                 @RequestParam("dimension") String dimension,
                                                 @RequestParam(value = "availableColors", required = false) List<String> availableColors,
                                                 @RequestParam(value = "availableMaterials", required = false) List<String> availableMaterials,
                                                 @RequestParam(value = "sizeOptions", required = false) List<String> sizeOptions,
                                                 @RequestParam(value = "image", required = false) MultipartFile image) {

        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        existingProduct.setName(name);
        existingProduct.setProductId(productId);
        existingProduct.setDescription(description);
        existingProduct.setPrice(price);
        existingProduct.setDimension(dimension);
        existingProduct.setAvailableColors(availableColors);
        existingProduct.setAvailableMaterials(availableMaterials);
        existingProduct.setSizeOptions(sizeOptions);

        if (image != null) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            String uploadDir = "uploads/";
            Path path = Paths.get(uploadDir + fileName);
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, image.getBytes());
                existingProduct.setImagePath(fileName);
            } catch (Exception e) {
                throw new RuntimeException("Failed to store file.", e);
            }
        }

        return ResponseEntity.ok(productService.saveProduct(existingProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(required = false) String query) {
        List<Product> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }
}
