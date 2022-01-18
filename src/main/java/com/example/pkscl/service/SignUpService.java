package com.example.pkscl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.repository.MajorRepository;
import com.example.pkscl.repository.PresidentRepository;
import com.example.pkscl.repository.StudentRepository;

@Service
public class SignUpService {

    private StudentRepository studentRepository;
    private PresidentRepository presidentRepository;
    private MajorRepository majorRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SignUpService(StudentRepository studentRepository, PresidentRepository presidentRepository, MajorRepository majorRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.presidentRepository = presidentRepository;
        this.majorRepository = majorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signUpStudent(Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setStatus("wait");
        studentRepository.save(student);
    }

    public void signUpPresident(President president) {
        president.setPassword(passwordEncoder.encode(president.getPassword()));
        president.setStatus("wait");
        presidentRepository.save(president);
    }

    public boolean studentCheckEmail(String email) {
        List<Student> studentList = studentRepository.findByEmail(email);
        if (studentList.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean presidentCheckEmail(String email) {
        List<President> presidentList = presidentRepository.findByEmail(email);
        if (presidentList.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void fileUpload(String filename, MultipartFile file) throws Exception {
        String path = System.getProperty("user.dir") + "/src/main/resources/static/certfiles/";
        File saveFile = new File(path + filename);
        file.transferTo(saveFile);
    }

    // majorstatus가 approval인 major의 majorname 목록 반환
    public List<String> getMajorList() {
        List<String> majorList = majorRepository.findByMajorstatus("approval").stream().map(major -> major.getMajorname()).collect(java.util.stream.Collectors.toList());
        return majorList;
    }
}