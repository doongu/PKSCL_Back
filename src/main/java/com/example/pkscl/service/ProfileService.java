package com.example.pkscl.service;

import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.major.Major;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.repository.PresidentRepository;
import com.example.pkscl.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.example.pkscl.repository.MajorRepository;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;


@Service
public class ProfileService {

    private final StudentRepository studentRepository;
    private final PresidentRepository presidentRepository;
    private final MajorRepository majorRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public ProfileService(StudentRepository studentRepository, PresidentRepository presidentRepository, MajorRepository majorRepository,  PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.presidentRepository = presidentRepository;
        this.majorRepository = majorRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Map<String,Object> getProfileData(String position, String email, String majorNumber) {

        Map<String,Object> profileInfo = new LinkedHashMap<>();
        // 학생을 리스트 형식으로 받아옴
        if(position == "student") {
            Student profileData = studentRepository.findByEmail(email);

            String studentId = profileData.getStudentid();
            String name = profileData.getName();
            String pEmail = profileData.getEmail();
            String certfilepath = profileData.getCertfilepath();

            LinkedHashMap<String, Object> certfile = new LinkedHashMap<>();
            certfile.put("name", certfilepath);

            profileInfo.put("stdID", studentId);
            profileInfo.put("major", majorNumber);
            profileInfo.put("name", name);
            profileInfo.put("email", pEmail);
            profileInfo.put("certFile", certfile);
        }

        else if(position == "president"){
            President profileData = presidentRepository.findByEmail(email);
            Major major = majorRepository.findByMajornumber(Integer.parseInt(majorNumber));

            String studentId = profileData.getStudentid();
            String name = profileData.getName();
            String phoneNumber = profileData.getPhonenumber();
            String pEmail = profileData.getEmail();
            String majorLogo = major.getMajorlogo();

            LinkedHashMap<String, Object> majorLogoMap = new LinkedHashMap<>();
            majorLogoMap.put("name", majorLogo);

            profileInfo.put("stdID", studentId);
            profileInfo.put("major", majorNumber);
            profileInfo.put("name", name);
            profileInfo.put("phoneNumber", phoneNumber);
            profileInfo.put("email", pEmail);
            profileInfo.put("majorLogo", majorLogoMap);
        }

        return profileInfo;

    }

    public void fileUploadStd(String filename, MultipartFile file) throws Exception {
        String path = System.getProperty("user.dir") + "/static/static/studentCertFile/";
        File saveFile = new File(path + filename);
        file.transferTo(saveFile);
    }

    public void fileUploadLogo(String filename, MultipartFile file) throws Exception {
        String path = System.getProperty("user.dir") + "/static/static/majorLogo/";
        File saveFile = new File(path + filename);
        file.transferTo(saveFile);
    }

    @Transactional
    public void putStudentProfileData(String email, String stdID, int major,String name, String certFilePath) {
        Student profileData = studentRepository.findByEmail(email);
        profileData.setStudentid(stdID);
        profileData.setMajornumber(major);
        profileData.setName(name);
        profileData.setStatus("waiting");
        if(certFilePath != null) {
            profileData.setCertfilepath("./static/studentCertFile/"+certFilePath);
        }
        studentRepository.save(profileData);
    }

    @Transactional
    public void putPresidentProfileData(String email, String stdID, String name, String phoneNumber, String majorLogoPath) {
        President profileData = presidentRepository.findByEmail(email);
        profileData.setStudentid(stdID);
        profileData.setName(name);
        profileData.setPhonenumber(phoneNumber);
        if(profileData.getStatus().equals("refusal")) {
            profileData.setStatus("waiting");
        }
        presidentRepository.save(profileData);

        if(majorLogoPath != null) {
            int majorNumber = presidentRepository.findByEmail(email).getMajornumber();
            Major major = majorRepository.findByMajornumber(majorNumber);
            major.setMajorlogo("./static/majorLogo/" + majorLogoPath);
            majorRepository.save(major);    
        }

    }

    public String getStudentPassword(String email) {
        Student student = studentRepository.findByEmail(email);
        return student.getPassword();
    }

    public String getPresidentPassword(String email){
        President president = presidentRepository.findByEmail(email);
        return president.getPassword();
    }

    public void patchStudentPassword(String email, String newPassword){
        Student student = studentRepository.findByEmail(email);
        student.setPassword(passwordEncoder.encode(newPassword));

        studentRepository.save(student);
    }

    public void patchPresidentPassword(String email, String newPassword){
        President president = presidentRepository.findByEmail(email);
        president.setPassword(passwordEncoder.encode(newPassword));

        presidentRepository.save(president);
    }
}