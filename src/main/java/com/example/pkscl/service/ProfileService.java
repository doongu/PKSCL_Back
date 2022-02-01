package com.example.pkscl.service;

import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.major.Major;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.repository.PresidentRepository;
import com.example.pkscl.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.pkscl.repository.MajorRepository;

import javax.transaction.Transactional;


@Service
public class ProfileService {

    private final StudentRepository studentRepository;
    private final PresidentRepository presidentRepository;
    private final MajorRepository majorRepository;


    @Autowired
    public ProfileService(StudentRepository studentRepository, PresidentRepository presidentRepository, MajorRepository majorRepository) {
        this.studentRepository = studentRepository;
        this.presidentRepository = presidentRepository;
        this.majorRepository = majorRepository;
    }


    public Map<String,Object> getProfileData(String position, String email, String majorNumber) {

        Map<String,Object> profileInfo = new LinkedHashMap<>();
        // 학생을 리스트 형식으로 받아옴
        if(position == "student") {
            Student profileData = studentRepository.findByEmail(email);
            Major major = majorRepository.findByMajornumber(majorNumber);

            String studentId = profileData.getStudentid();
            String majorName = major.getMajorname();
            String name = profileData.getName();
            String pEmail = profileData.getEmail();
            String certfilepath = profileData.getCertfilepath();

            profileInfo.put("stdID", studentId);
            profileInfo.put("major", majorName);
            profileInfo.put("name", name);
            profileInfo.put("email", pEmail);
            profileInfo.put("majorLogo", certfilepath);
        }

        else if(position == "president"){
            President profileData = presidentRepository.findByEmail(email);
            Major major = majorRepository.findByMajornumber(majorNumber);

            String studentId = profileData.getStudentid();
            String majorName = major.getMajorname();
            String name = profileData.getName();
            String phoneNumber = profileData.getPhonenumber();
            String pEmail = profileData.getEmail();
           // String majorLogo = profileData.getMajorlogo();

            profileInfo.put("stdID", studentId);
            profileInfo.put("major", majorName);
            profileInfo.put("name", name);
            profileInfo.put("phoneNumber", phoneNumber);
            profileInfo.put("email", pEmail);
           // profileInfo.put("majorLogo", majorLogo);
        }

        return profileInfo;

    }
    @Transactional
     public void putStudentProfileData(String email, String stdID, int major,String name) {
            Student profileData = studentRepository.findByEmail(email);
            profileData.setStudentid(stdID);
            profileData.setMajornumber(major);
            profileData.setName(name);
            studentRepository.save(profileData);
     }

    @Transactional
     public void putPresidentProfileData(String email, String stdID, String name, String phoneNumber) {
            President profileData = presidentRepository.findByEmail(email);
            profileData.setStudentid(stdID);
            profileData.setName(name);
            profileData.setPhonenumber(phoneNumber);
            presidentRepository.save(profileData);
    }
    
}