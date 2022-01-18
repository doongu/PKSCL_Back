package com.example.pkscl.repository;

import java.util.List;

import com.example.pkscl.domain.member.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    List<Admin> findById(String id);
}