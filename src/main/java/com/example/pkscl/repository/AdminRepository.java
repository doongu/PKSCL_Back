package com.example.pkscl.repository;

import com.example.pkscl.domain.member.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findById(String id);
}