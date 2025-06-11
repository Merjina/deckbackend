package com.example.deck.service;

import com.example.deck.model.User;
import com.example.deck.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService; // Ensure this service is created

    @PostConstruct
    public void seedAdmin() {
        Optional<User> adminUser = userRepository.findByEmail("admin@example.com");
        if (adminUser.isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@example.com");
            admin.setPhone("1234567890");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("Admin user seeded successfully.");
        }
    }
    // Method to save or update the user (this will persist the updated user profile)
    public User saveUser(User user) {
        return userRepository.save(user);  // Save the user to the database
    }

    // Method to update user profile (including additional fields like address, location, pincode)
    public User updateUserProfile(Long id, User userDetails) {
        Optional<User> existingUser = userRepository.findById(id);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setPhone(userDetails.getPhone());
            user.setAddress(userDetails.getAddress());  // New address field
            user.setLocation(userDetails.getLocation());  // New location field
            user.setPincode(userDetails.getPincode());  // New pincode field

            return userRepository.save(user);  // Save the updated user
        }

        throw new RuntimeException("User not found");
    }
    
    public ResponseEntity<?> processForgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
        }

        String resetToken = UUID.randomUUID().toString();
        User user = userOptional.get();
        user.setResetToken(resetToken);
        userRepository.save(user);

        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
        emailService.sendEmail(email, "Password Reset Request", "Click here to reset your password: " + resetLink);

        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }
    
    public ResponseEntity<?> resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Invalid or expired token"));
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successful"));
    }

    public User register(User user) {
        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
        Optional<User> existingUserByMobile = userRepository.findByPhone(user.getPhone());

        if (existingUserByEmail.isPresent() || existingUserByMobile.isPresent()) {
            throw new RuntimeException("User already exists with that email or mobile number.");
        }

        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        return userRepository.save(user);
    }

    public boolean validateLogin(String identifier, String rawPassword) {
        Optional<User> optionalUser = identifier.contains("@") ? userRepository.findByEmail(identifier) : userRepository.findByPhone(identifier);
        return optionalUser.isPresent() && passwordEncoder.matches(rawPassword, optionalUser.get().getPassword());
    }

    public Optional<User> getUserByIdentifier(String identifier) {
        return identifier.contains("@") ? userRepository.findByEmail(identifier) : userRepository.findByPhone(identifier);
    }
    
    
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public List<User> getUsersByRole(String role) {
//        return userRepository.findByRole(role);
//    }
//    
//    public boolean deleteUserById(Long id) {
//        Optional<User> user = userRepository.findById(id);
//        if (user.isPresent()) {
//            userRepository.deleteById(id);
//            return true;
//        }
//        return false;
//    }
    
    
    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }}

