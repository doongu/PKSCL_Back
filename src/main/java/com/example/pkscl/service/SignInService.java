package com.example.pkscl.service;

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
        President president = presidentRepository.findByEmail(email);
        if(president == null) return false;
        return passwordEncoder.matches(password, president.getPassword());
    }

    public boolean studentMatch(String password, String email) {
        Student student = studentRepository.findByEmail(email);
        if (student == null) return false;
        return passwordEncoder.matches(password, student.getPassword());
    }

    public boolean adminMatch(String password, String id) {
        Admin admin = adminRepository.findById(id);
        if (admin == null) return false;
        return passwordEncoder.matches(password, admin.getPassword());
    }

    //이메일로 부터 학생 학과번호 가져오기
    public int getStudentMajor(String email) {
        Student student = studentRepository.findByEmail(email);
        if(student == null) return -1;
        return student.getMajornumber();
    }

    public int getPresidentMajor(String email) {
        President president = presidentRepository.findByEmail(email);
        if (president == null) return -1;
        return president.getMajornumber();
    }

    public String getStudentStatus(String email) {
        Student student = studentRepository.findByEmail(email);
        if (student == null) return "";
        return student.getStatus();
    }

    public String getPresidentStatus(String email) {
        President president = presidentRepository.findByEmail(email);
        if (president == null) return "";
        return president.getStatus();
    }

    public void withdrawalStudent(String email) {
        Student student = studentRepository.findByEmail(email);
        studentRepository.delete(student);
    }

    public void withdrawalPresident(String email) {
        President president = presidentRepository.findByEmail(email);
        presidentRepository.delete(president);
    }

}
