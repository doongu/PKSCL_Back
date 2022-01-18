package com.example.pkscl.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import com.example.pkscl.domain.major.Major;
import com.example.pkscl.domain.member.Admin;
import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.repository.AdminRepository;
import com.example.pkscl.repository.MajorRepository;
import com.example.pkscl.repository.PresidentRepository;
import com.example.pkscl.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.event.PublicInvocationEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignInService {
    
    private final PasswordEncoder passwordEncoder;
    private final PresidentRepository presidentRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final MajorRepository majorRepository;

    @Autowired
    public SignInService(PasswordEncoder passwordEncoder, PresidentRepository presidentRepository, StudentRepository studentRepository, AdminRepository adminRepository, MajorRepository majorRepository) {
        this.passwordEncoder = passwordEncoder;
        this.presidentRepository = presidentRepository;
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
        this.majorRepository = majorRepository;
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

    //이메일로 부터 학생 학과번호 가져오기
    public int getStudentMajor(String email) {
        Optional<Student> student = studentRepository.findByEmail(email).stream().findFirst();
        if (!student.isPresent()) return 0;
        return student.get().getMajornumber();
    }

    public int getPresidentMajor(String email) {
        Optional<President> president = presidentRepository.findByEmail(email).stream().findFirst();
        if (!president.isPresent()) return 0;
        return president.get().getMajornumber();
    }

    // 해당 학과의 major 정보 반환
    public LinkedHashMap<String, Object> getPresidentInfo(String majorNumber) {

        LinkedHashMap<String, Object> studentPresident = new LinkedHashMap<>();
        Major major = majorRepository.findByMajornumber(majorNumber);
        String majorName = major.getMajorname();
        String name = major.getName();
        String phoneNumber = major.getPhonenumber();
        String email = major.getEmail();

        studentPresident.put("major", majorName);
        studentPresident.put("name", name);
        studentPresident.put("phoneNumber", phoneNumber);
        studentPresident.put("email", email);

        return studentPresident;
    }

}
