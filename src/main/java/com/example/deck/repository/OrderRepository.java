package com.example.deck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.deck.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
