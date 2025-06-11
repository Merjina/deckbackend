package com.example.deck.repository;

import com.example.deck.model.Message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
//    List<Message> findByUserEmail(String email);
}
