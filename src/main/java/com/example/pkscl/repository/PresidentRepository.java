package com.example.pkscl.repository;

import java.util.List;

import com.example.pkscl.domain.member.President;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresidentRepository extends JpaRepository<President, Long> {
    List<President> findByEmail(String email);
}