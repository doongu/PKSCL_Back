package com.example.pkscl.repository;

import java.util.List;

import com.example.pkscl.domain.member.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);
    List<Student> findByMajornumber(int majornumber);
}