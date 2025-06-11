//package com.example.deck.controller;
//package com.example.deck.controller;
//
//import com.example.deck.dto.MessageRequest;
//import com.example.deck.model.Message;
//import com.example.deck.service.MessageService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/messages")
//public class MessageController {
//
//    @Autowired
//    private MessageService messageService;
//
//    @PostMapping("/send")
//    public Message sendMessage(@RequestHeader("userEmail") String email, @RequestBody MessageRequest request) {
//        return messageService.sendMessage(email, request);
//    }
//
//    @GetMapping("/admin/all")
//    public List<Message> getAllMessages() {
//        return messageService.getAllMessages(); // Only admin should call this
//    }
//
//    @GetMapping("/user")
//    public List<Message> getUserMessages(@RequestHeader("userEmail") String email) {
//        return messageService.getMessagesBySender(email);
//    }
//}
