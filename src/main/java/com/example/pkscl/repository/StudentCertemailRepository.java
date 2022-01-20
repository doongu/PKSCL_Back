package com.example.pkscl.repository;

import com.example.pkscl.domain.temp.StudentCertemail;

import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentCertemailRepository extends JpaRepository<StudentCertemail, Long> {
    StudentCertemail findByEmail(String email);
}
