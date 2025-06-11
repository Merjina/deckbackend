package com.example.deck.controller;

import com.example.deck.dto.ContactDto;
import com.example.deck.dto.ContactDto1;
import com.example.deck.model.Contact;
import com.example.deck.model.Notification;
import com.example.deck.repository.ContactRepository;
import com.example.deck.repository.NotificationRepository;
import com.example.deck.security.JwtUtil;
import com.example.deck.service.ContactService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/send")
    public ResponseEntity<?> submitContactMessage(@RequestBody ContactDto dto,
                                                  @RequestHeader("Authorization") String authHeader) {
        // Check the received DTO for correct field mapping
        System.out.println("Received ContactDto: " + dto);  // Log DTO to ensure preferredDate is populated

        String token = authHeader.substring(7); // Remove "Bearer "
        String userEmail = jwtUtil.extractUsername(token);

        try {
            contactService.submitContactForm(dto, userEmail);
            return ResponseEntity.ok("Message submitted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

//    @PutMapping("/{id}/edit-approval")
//    public ResponseEntity<String> editApproval(@PathVariable Long id, @RequestParam String status) {
//        try {
//            contactService.editApprovalStatus(id, status);
//            return ResponseEntity.ok("Approval status updated.");
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
//        }
//    }

    @PutMapping("/{id}/edit-datetime")
    public ResponseEntity<String> updateDateTime(@PathVariable Long id, @RequestBody ContactDto1 dto) {
        Optional<Contact> optionalContact = contactRepository.findById(id);
        if (!optionalContact.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contact message not found");
        }

        try {
            Contact contact = optionalContact.get();
            contact.setPreferredDate(LocalDate.parse(dto.getPreferredDate())); // parses "2025-05-01"
            contact.setStartTime(LocalTime.parse(dto.getStartTime()));         // parses "10:00"
            contact.setEndTime(LocalTime.parse(dto.getEndTime()));             // parses "11:00"
            contactRepository.save(contact);

            return ResponseEntity.ok("Date and time updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date or time format");
        }
    }


    @GetMapping("/all")
    public List<Contact> getAll() {
        return contactService.getAllMessages();
    }
    
    @GetMapping("/approved")
    public ResponseEntity<List<Contact>> getApprovedMessages() {
        List<Contact> approvedMessages = contactService.getApprovedMessages();
        return ResponseEntity.ok(approvedMessages);
    }
    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveContact(@PathVariable Long id) {
        Contact msg = contactService.approveContact(id); // update method to return Contact
        Notification notification = new Notification();
        notification.setUserEmail(msg.getEmail());
        notification.setMessage("Your contact request has been approved!");
        notification.setReadStatus(false);
        notificationRepository.save(notification);
        return ResponseEntity.ok("Contact approved and notification sent.");
    }


  
    @GetMapping("/notifications/user/{email}")
    public List<Notification> getUserNotifications(@PathVariable String email) {
        return notificationRepository.findByUserEmail(email);
    }
   


}
