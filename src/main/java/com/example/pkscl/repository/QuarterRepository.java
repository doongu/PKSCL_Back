package com.example.pkscl.repository;

import java.util.List;

import com.example.pkscl.domain.ledger.Quarter;
import com.example.pkscl.domain.ledger.QuarterKey;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuarterRepository extends JpaRepository<Quarter, QuarterKey> {
    // findByMajornumberAndQuarternumber
    List<Quarter> findByMajornumber(int majornumber);
}
