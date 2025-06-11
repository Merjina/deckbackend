package com.example.deck.repository;

import com.example.deck.model.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    List<Quotation> findByStatusIgnoreCase(String status);
    List<Quotation> findByCustomerEmail(String customerEmail); // 🔥 Add this line
}
