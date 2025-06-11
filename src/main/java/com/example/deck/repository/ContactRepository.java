package com.example.deck.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deck.model.Contact;
import com.example.deck.model.Contact.Status;

public interface ContactRepository extends JpaRepository<Contact, Long> {
	 List<Contact> findByStatus(String status);
	 List<Contact> findByStatus(Status status);
	 
}
