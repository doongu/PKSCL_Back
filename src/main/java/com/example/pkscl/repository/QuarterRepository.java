package com.example.pkscl.repository;

import java.util.List;

import com.example.pkscl.domain.ledger.Quarter;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuarterRepository extends JpaRepository<Quarter, Long> {
    List<Quarter> findByMajornumber(int majornumber);
    Quarter findByMajornumberAndQuarternumber(int majornumber, String quarternumber);
    Quarter findByQuarterid(int quarterid);
}
