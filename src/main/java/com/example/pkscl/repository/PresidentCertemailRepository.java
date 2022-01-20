package com.example.pkscl.repository;

import com.example.pkscl.domain.temp.PresidentCertemail;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PresidentCertemailRepository extends JpaRepository<PresidentCertemail, Long> {
    PresidentCertemail findByEmail(String email);
}
