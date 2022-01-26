package com.example.pkscl.repository;

import com.example.pkscl.domain.member.President;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresidentRepository extends JpaRepository<President, Long> {
    President findByEmail(String email);
}