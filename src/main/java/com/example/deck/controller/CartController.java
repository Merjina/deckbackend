package com.example.deck.controller;

import com.example.deck.model.Cart;
import com.example.deck.security.JwtUtil;
import com.example.deck.service.CartService;
import com.example.deck.service.WishlistService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:3000","https://deck-7kf5-merjinas-projects.vercel.app","https://deck-7kf5.vercel.app"}) // Allow your React app to make requests

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WishlistService wishlistService;

    // ✅ Add item to the cart
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody Cart cartItem, HttpServletRequest request) {
        try {
            String token = jwtUtil.getTokenFromRequest(request);
            if (token == null || jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String username = jwtUtil.extractUsername(token);
            cartItem.setUsername(username); // Attach the username from token
            Cart addedItem = cartService.addToCart(cartItem);
            return ResponseEntity.ok(addedItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // ✅ Get cart items for the logged-in user
    @GetMapping
    public List<Cart> getCartItems(HttpServletRequest request) {
        String token = jwtUtil.getTokenFromRequest(request);
        if (token == null || jwtUtil.isTokenExpired(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String username = jwtUtil.extractUsername(token);
        return cartService.getCartItemsByUser(username);
    }

    // ✅ Remove item from cart (by productId + username)
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long productId, HttpServletRequest request) {
        String token = jwtUtil.getTokenFromRequest(request);
        if (token == null || jwtUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String username = jwtUtil.extractUsername(token);
        boolean removed = cartService.removeFromCart(productId, username);
        if (removed) {
            return ResponseEntity.ok("Item removed from cart");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found in cart");
        }
    }

    // ✅ Update quantity
    @PutMapping("/update/{id}")
    public Cart updateCartQuantity(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return cartService.updateCartQuantity(id, payload.get("action"));
    }

    // ✅ Move from wishlist to cart
    @PostMapping("/move-from-wishlist/{productId}")
    public ResponseEntity<Cart> moveFromWishlistToCart(@PathVariable Long productId, HttpServletRequest request) {
        String token = jwtUtil.getTokenFromRequest(request);
        if (token == null || jwtUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = jwtUtil.extractUsername(token);
        Cart cartItem = cartService.moveFromWishlistToCart(productId, username);

        if (cartItem != null) {
            return ResponseEntity.ok(cartItem);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
