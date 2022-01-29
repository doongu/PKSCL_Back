package com.example.pkscl.repository;

import java.util.List;

import com.example.pkscl.domain.ledger.Receipt;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByEventnumber(int eventnumber);
}
