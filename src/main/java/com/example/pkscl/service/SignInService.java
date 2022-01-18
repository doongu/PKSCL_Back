package com.example.pkscl.service;

import java.util.List;
import java.util.Optional;

import com.example.pkscl.domain.member.Admin;
import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.repository.AdminRepository;
import com.example.pkscl.repository.PresidentRepository;
import com.example.pkscl.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignInService {
    
    private final PasswordEncoder passwordEncoder;
    private final PresidentRepository presidentRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public SignInService(PasswordEncoder passwordEncoder, PresidentRepository presidentRepository, StudentRepository studentRepository, AdminRepository adminRepository) {
        this.passwordEncoder = passwordEncoder;
        this.presidentRepository = presidentRepository;
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
    }

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    // 비밀번호 일치 여부 확인
    public boolean presidentMatch(String password, String email) {
        Optional<President> president = presidentRepository.findByEmail(email).stream().findFirst();
        if (!president.isPresent()) return false;
        return passwordEncoder.matches(password, president.get().getPassword());
    }

    public boolean studentMatch(String password, String email) {
        Optional<Student> student = studentRepository.findByEmail(email).stream().findFirst();
        if (!student.isPresent()) return false;
        return passwordEncoder.matches(password, student.get().getPassword());
    }

    public boolean adminMatch(String password, String id) {
        Optional<Admin> admin = adminRepository.findById(id).stream().findFirst();
        if(!admin.isPresent()) return false;
        return passwordEncoder.matches(password, admin.get().getPassword());
    }

}
