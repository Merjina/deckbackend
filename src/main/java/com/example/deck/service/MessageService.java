package com.example.deck.service;

import com.example.deck.model.Message;
import com.example.deck.model.User;
import com.example.deck.repository.MessageRepository;
import com.example.deck.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public Message submitMessage(Long userId, String comment, int rating) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Message message = new Message(comment, rating, user.get());
            return messageRepository.save(message);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
}
