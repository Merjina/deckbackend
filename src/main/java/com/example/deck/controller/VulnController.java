package com.example.deck.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Paths;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/vuln")
public class VulnController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 🛑 SQL Injection: accepts raw user input directly in query
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = '" + email + "' AND password = '" + password + "'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return ResponseEntity.ok(count > 0 ? "Login success" : "Login failed");
    }

    // 🛑 XSS: echoes unsanitized input in response
    @GetMapping("/comment")
    public ResponseEntity<String> comment(@RequestParam String message) {
        return ResponseEntity.ok("<h3>User comment:</h3> " + message);
    }

    // 🛑 CSRF: no token check, modifies data directly
    @PostMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@RequestParam String name) {
        return ResponseEntity.ok("Profile updated to: " + name);
    }

    // 🛑 SSRF: fetches remote URLs based on user input
    @GetMapping("/fetch-url")
    public ResponseEntity<String> fetchUrl(@RequestParam String url) {
        try {
            URI uri = new URI(url);
            Resource resource = new UrlResource(uri);
            return ResponseEntity.ok("Fetched URL: " + resource.getURI());
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body("Invalid URL");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error fetching URL");
        }
    }

    // 🛑 Email Disclosure: error message leaks email
    @GetMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return ResponseEntity.badRequest().body("Error: No user found for email " + email);
    }

    // 🛑 Broken Access Control: anyone can access admin-only data
    @GetMapping("/admin-panel")
    public ResponseEntity<String> getAdminData() {
        return ResponseEntity.ok("Sensitive admin data: Revenue = ₹1,00,00,000");
    }

    // 🛑 Cryptographic Failure: weak hashing (MD5)
    @PostMapping("/hash")
    public ResponseEntity<String> weakHash(@RequestParam String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // insecure
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder hash = new StringBuilder();
            for (byte b : hashBytes) hash.append(String.format("%02x", b));
            return ResponseEntity.ok("MD5 Hash: " + hash.toString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error hashing password");
        }
    }

    // 🛑 Security Misconfiguration: directory listing simulation
    @GetMapping("/list-files")
    public ResponseEntity<String> listFiles() {
        return ResponseEntity.ok("Directory contents: index.html, config.yaml, .env");
    }

    // 🛑 HTML Comment with email
    @GetMapping("/footer")
    public ResponseEntity<String> footerWithDisclosure() {
        String content = "<!-- Contact: support@deckline.com -->\n<footer>Deckline ©2025</footer>";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html");
        return ResponseEntity.ok().headers(headers).body(content);
    }
}
