package com.example.pkscl.repository;

import java.util.List;

import com.example.pkscl.domain.ledger.Receiptdetail;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptdetailRepository extends JpaRepository<Receiptdetail, Long> {
    List<Receiptdetail> findByReceiptnumber(int receiptnumber);
}
