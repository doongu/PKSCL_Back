package com.example.pkscl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.pkscl.domain.major.Major;
import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.domain.temp.PresidentCertemail;
import com.example.pkscl.domain.temp.StudentCertemail;
import com.example.pkscl.repository.StudentCertemailRepository;
import com.example.pkscl.repository.MajorRepository;
import com.example.pkscl.repository.PresidentCertemailRepository;
import com.example.pkscl.repository.PresidentRepository;
import com.example.pkscl.repository.StudentRepository;

@Service
public class SignUpService {

    private StudentRepository studentRepository;
    private PresidentRepository presidentRepository;
    private StudentCertemailRepository studentCertemailRepository;
    private PresidentCertemailRepository presidentCertemailRepository;
    private MajorRepository majorRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SignUpService(StudentRepository studentRepository, PresidentRepository presidentRepository, StudentCertemailRepository studentCertemailRepository, PresidentCertemailRepository presidentCertemailRepository, MajorRepository majorRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.presidentRepository = presidentRepository;
        this.studentCertemailRepository = studentCertemailRepository;
        this.presidentCertemailRepository = presidentCertemailRepository;
        this.majorRepository = majorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean signUpStudent(Student student) {
        StudentCertemail certemail = studentCertemailRepository.findByEmail(student.getEmail());
        if(certemail == null || certemail.getStatus() == 0) return false;
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setStatus("waiting");
        studentRepository.save(student);
        return true;
    }

    public boolean signUpPresident(President president) {
        PresidentCertemail certemail = presidentCertemailRepository.findByEmail(president.getEmail());
        if(certemail == null || certemail.getStatus() == 0) return false;
        president.setPassword(passwordEncoder.encode(president.getPassword()));
        president.setStatus("waiting");
        presidentRepository.save(president);
        return true;
    }

    public boolean studentCheckEmail(String email) {
        Student student = studentRepository.findByEmail(email);
        if(student != null) return false;
        return true;
    }

    public boolean presidentCheckEmail(String email) {
        President president = presidentRepository.findByEmail(email);
        if(president != null) return false;
        return true;
    }

    public void fileUpload(String filename, MultipartFile file) throws Exception {
        String path = System.getProperty("user.dir") + "/static/static/studentCertFile/";
        File saveFile = new File(path + filename);
        file.transferTo(saveFile);
    }

    // major의 majorname 목록 반환
    public Map<String,Object> getMajorList() {
        List<String> majorList = majorRepository.findAll().stream().map(Major::getMajorname).collect(Collectors.toList());
        LinkedHashMap<String,Object> result = new LinkedHashMap<>();
        result.put("majorList", majorList);
        return result;
    }
}