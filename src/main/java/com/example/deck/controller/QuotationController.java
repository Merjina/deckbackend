package com.example.deck.controller;

import com.example.deck.dto.QuotationDTO;
import com.example.deck.dto.QuotationRequest;
import com.example.deck.model.Quotation;
import com.example.deck.security.JwtUtil;
import com.example.deck.service.QuotationService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = {"http://localhost:3000","https://deck-7kf5-merjinas-projects.vercel.app","https://deck-7kf5.vercel.app"}) // Allow your React app to make requests

public class QuotationController {

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private JwtUtil jwtUtil;

    // 📄 Create Quotation with PDF Generation
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createQuotation(@RequestBody QuotationRequest request) {
        Quotation quotation = new Quotation();
        quotation.setCustomerName(request.getCustomerName());
        quotation.setCustomerEmail(request.getCustomerEmail());
        quotation.setProductId(String.valueOf(request.getProductId()));
        quotation.setCustomOptions(request.getCustomOptions());
        quotation.setProductName(request.getProductName());
        quotation.setQuantity(request.getQuantity());
        quotation.setQuotedPrice(request.getQuotedPrice());

        Quotation saved = quotationService.saveQuotation(quotation, true);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Quotation created successfully");
        response.put("pdfFileName", "Quotation_" + saved.getId() + ".pdf");
        return ResponseEntity.ok(response);
    }

    // 📥 Download Quotation PDF
    @GetMapping("/pdf/{fileName}")
    public ResponseEntity<InputStreamResource> downloadQuotationPDF(@PathVariable String fileName) throws IOException {
        File file = new File("src/main/resources/quotations/" + fileName);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(file.length())
                .body(resource);
    }

    // 🔍 Get All Quotations
    @GetMapping
    public ResponseEntity<List<Quotation>> getAllQuotations() {
        return ResponseEntity.ok(quotationService.getAllQuotations());
    }

    // 🔍 Get Quotation by ID
    @GetMapping("/{id}")
    public ResponseEntity<Quotation> getQuotationById(@PathVariable Long id) {
        Quotation quotation = quotationService.getQuotationById(id);
        return quotation != null ? ResponseEntity.ok(quotation) : ResponseEntity.notFound().build();
    }

    // ✅ Approve Quotation
    @PostMapping("/{id}/approve")
    public ResponseEntity<Quotation> approveQuotation(@PathVariable Long id) {
        Quotation updated = quotationService.approveQuotation(id);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // ❌ Reject Quotation
    @PostMapping("/{id}/reject")
    public ResponseEntity<Quotation> rejectQuotation(@PathVariable Long id, @RequestParam(required = false) String reason) {
        Quotation updated = quotationService.rejectQuotation(id, reason);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // 🔎 Filter by status (PENDING, APPROVED, REJECTED)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Quotation>> getQuotationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(quotationService.getQuotationsByStatus(status));
    }

    // ❌ Delete Quotation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable Long id) {
        quotationService.deleteQuotation(id);
        return ResponseEntity.ok().build();
    }
 // 🔐 Get Quotations for Authenticated User
    @GetMapping("/user")
    public ResponseEntity<List<Quotation>> getUserQuotations(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);  // 👈 correct usage
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.extractUsername(token);
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Quotation> quotations = quotationService.getQuotationsByEmail(email);
        return ResponseEntity.ok(quotations);
    }

}
