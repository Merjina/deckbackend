package com.example.deck.controller;

import com.example.deck.model.User;
import com.example.deck.security.JwtUtil;
import com.example.deck.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000","https://deck-7kf5-merjinas-projects.vercel.app"}) // Allow your React app to make requests
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return userService.processForgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        return userService.resetPassword(token, newPassword);
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> registerUser(@RequestBody User user) {
        try {
            userService.register(user);
            return ResponseEntity.ok(new ResponseMessage("User registered successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userService.getUserByIdentifier(loginRequest.getIdentifier());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (userService.validateLogin(loginRequest.getIdentifier(), loginRequest.getPassword())) {
                String token = jwtUtil.generateToken(user.getEmail()); // Generate JWT token
                boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
                return ResponseEntity.ok(new AuthResponse(token, isAdmin ? "ADMIN" : "USER"));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Invalid email or password."));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String token) {
        try {
            // Extract the token from the request header (assuming the prefix "Bearer ")
            String jwtToken = token.substring(7); // Remove "Bearer " prefix

            // Extract username (email) from the token
            String email = jwtUtil.extractUsername(jwtToken);

            Optional<User> userOptional = userService.getUserByIdentifier(email);
            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional.get()); // Return user details
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    // DELETE User Endpoint for Admin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // Endpoint to update the user profile (including address, location, pincode)
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody User user, @RequestHeader("Authorization") String token) {
        // Extract email from JWT token
        String email = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " from the token
        
        Optional<User> optionalUser = userService.getUserByIdentifier(email);
        if (optionalUser.isPresent()) {
            User updatedUser = optionalUser.get();
            updatedUser.setName(user.getName());
            updatedUser.setPhone(user.getPhone());
            updatedUser.setAddress(user.getAddress());  // Adding the address field
            updatedUser.setLocation(user.getLocation());  // Adding the location field
            updatedUser.setPincode(user.getPincode());  // Adding the pincode field

            userService.saveUser(updatedUser); // Save the updated user

            return ResponseEntity.ok("Profile updated successfully");
        }

        return ResponseEntity.status(404).body("User not found");
    }

    // Endpoint to get the current logged-in user's profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        // Extract email from JWT token
        String email = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " from the token

        Optional<User> optionalUser = userService.getUserByIdentifier(email);
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.get());  // Return the user data as JSON
        }

        return ResponseEntity.status(404).body("User not found");
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to fetch users.");
        }
    }

}

// DTO for login request
class LoginRequest {
    private String identifier; // Could be email or phone
    private String password;

    // Getters and Setters
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

// DTO for response message
class ResponseMessage {
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

// AuthResponse to send back the token and role
class AuthResponse {
    private String token;
    private String role;

    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }
}
