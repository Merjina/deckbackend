package com.example.deck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.deck.model.Order;
import com.example.deck.service.OrderService;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000","https://deck-7kf5-merjinas-projects.vercel.app","https://deck-7kf5.vercel.app"}) // Allow your React app to make requests

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public Order placeOrder(@RequestBody Order order) {
        return orderService.placeOrder(order);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
