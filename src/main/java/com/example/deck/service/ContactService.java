package com.example.deck.service;

import com.example.deck.dto.ContactDto;
import com.example.deck.model.Contact;
import com.example.deck.model.Contact.Status;
import com.example.deck.model.User;
import com.example.deck.repository.ContactRepository;
import com.example.deck.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepo;

    @Autowired
    private UserRepository userRepo;

    public void submitContactForm(ContactDto dto, String userEmail) {
        Optional<User> userOpt = userRepo.findByEmail(userEmail);
        if (userOpt.isEmpty()) throw new RuntimeException("User not found");

        User user = userOpt.get();

        Contact message = new Contact();
        message.setSubject(dto.getSubject());
        message.setMessage(dto.getMessage());
        message.setAppointmentType(dto.getAppointmentType());
        message.setPreferredDate(dto.getPreferredDate());
        message.setStartTime(dto.getStartTime());
        message.setEndTime(dto.getEndTime());
        message.setName(user.getName());
        message.setEmail(user.getEmail());
        message.setPhone(user.getPhone());
        message.setStatus(Status.PENDING);

        contactRepo.save(message);
    }

    public List<Contact> getAllMessages() {
        return contactRepo.findAll();
    }

    public Contact approveContact(Long id) {
        Contact msg = contactRepo.findById(id).orElseThrow();
        msg.setStatus(Status.APPROVED);
        return contactRepo.save(msg);
    }
    public List<Contact> getApprovedMessages() {
        return contactRepo.findByStatus(Status.APPROVED);
    }

   
//    public void editApprovalStatus(Long id, String statusStr) {
//        Contact msg = contactRepo.findById(id).orElseThrow();
//        Status status = Status.valueOf(statusStr.toUpperCase());
//        msg.setStatus(status);
//        contactRepo.save(msg);
//    }
}
