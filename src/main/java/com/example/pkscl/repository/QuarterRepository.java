package com.example.pkscl.repository;

import com.example.pkscl.domain.ledger.Quarter;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuarterRepository extends JpaRepository<Quarter, Long> {
    
}
