package com.example.deck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.deck.model.Cart;
import com.example.deck.model.WishList;
import com.example.deck.repository.CartRepository;
import com.example.deck.repository.WishlistRepository;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    // ✅ Get all cart items for a specific user
    public List<Cart> getCartItemsByUser(String username) {
        return cartRepository.findByUsername(username);
    }

    // ✅ Add item to cart (prevent duplicates, user-specific)
    public Cart addToCart(Cart cartItem) {
        if (cartItem.getProductId() == null || cartItem.getUsername() == null) {
            throw new RuntimeException("Product ID and Username cannot be null!");
        }

        Cart existingCartItem = cartRepository.findByProductIdAndUsername(cartItem.getProductId(), cartItem.getUsername());

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
            return cartRepository.save(existingCartItem);
        } else {
            cartItem.setQuantity(1);
            return cartRepository.save(cartItem);
        }
    }

    // ✅ Get all cart items (admin use case maybe?)
    public List<Cart> getCartItems() {
        return cartRepository.findAll();
    }

    // ✅ Remove from cart (user-specific)
    public boolean removeFromCart(Long productId, String username) {
        Cart item = cartRepository.findByProductIdAndUsername(productId, username);
        if (item != null) {
            cartRepository.deleteByProductIdAndUsername(productId, username);
            return true;
        }
        return false;
    }

    // ✅ Update cart quantity (by cart item ID)
    public Cart updateCartQuantity(Long id, String action) {
        Cart cartItem = cartRepository.findById(id).orElseThrow(() -> new RuntimeException("Cart item not found"));

        if ("increase".equals(action)) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else if ("decrease".equals(action) && cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
        }

        return cartRepository.save(cartItem);
    }

    // ✅ Move item from Wishlist to Cart
    public Cart moveFromWishlistToCart(Long productId, String username) {
        WishList wishlistItem = wishlistRepository.findByProductIdAndUsername(productId, username);

        if (wishlistItem == null) {
            throw new RuntimeException("Wishlist item not found!");
        }

        Cart existingCartItem = cartRepository.findByProductIdAndUsername(productId, username);
        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
            wishlistRepository.deleteByProductIdAndUsername(productId, username);
            return cartRepository.save(existingCartItem);
        } else {
            Cart newCartItem = new Cart();
            newCartItem.setProductId(wishlistItem.getProductId());
            newCartItem.setName(wishlistItem.getName());
            newCartItem.setImagePath(wishlistItem.getImagePath());
            newCartItem.setPrice(wishlistItem.getPrice());
            newCartItem.setQuantity(1);
            newCartItem.setUsername(username);

            wishlistRepository.deleteByProductIdAndUsername(productId, username);
            return cartRepository.save(newCartItem);
        }
    }
}
