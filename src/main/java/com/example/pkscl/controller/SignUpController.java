package com.example.pkscl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.member.PresidentModel;
import com.example.pkscl.domain.member.StudentModel;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.service.SignUpService;

@RestController
public class SignUpController {

    private final SignUpService signUpService;
    
    @Autowired
    public SignUpController(SignUpService signUpService) {
        this.signUpService = signUpService;
    }

    @PostMapping(value = "/signup/student")
    public void signUpStudent(@ModelAttribute StudentModel studentModel, MultipartFile certFile, HttpServletResponse response) throws Exception {

        Student student = new Student();
        student.setEmail(studentModel.getEmail());
        String password = studentModel.getPassword();

        // 401 Unauthorized
        if(!password.equals(studentModel.getCheckPassword())) {
            response.setStatus(401);
            return;
        }
        student.setPassword(password);
        student.setMajornumber(studentModel.getMajor());
        student.setStudentid(studentModel.getStdID());
        student.setName(studentModel.getName());
        
        // file_name을 현재시간을 기준으로 yyyyMMddHHmmssSSS.jpg 형태로 설정
        String filename = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
        String ext = certFile.getOriginalFilename().substring(certFile.getOriginalFilename().lastIndexOf("."));
        student.setCertfilepath(filename+ext);

        //중복확인후 400반환
        if(!signUpService.studentCheckEmail(student.getEmail())) {
            response.setStatus(409);
            return;
        }

        signUpService.fileUpload(filename+ext, certFile);
        if(!signUpService.signUpStudent(student)) {
            response.setStatus(403);
        }
        
    }

    @PostMapping(value = "/signup/president")
    public void signUpPresident(@ModelAttribute PresidentModel presidentModel, MultipartFile certFile, HttpServletResponse response) throws Exception {

        President president = new President();
        president.setEmail(presidentModel.getEmail());
        String password = presidentModel.getPassword();

        // 401 Unauthorized
        if(!password.equals(presidentModel.getCheckPassword())) {
            response.setStatus(401);
            return;
        }
        president.setPassword(password);
        president.setName(presidentModel.getName());
        president.setMajornumber(presidentModel.getMajor());
        president.setStudentid(presidentModel.getStdID());
        president.setPhonenumber(presidentModel.getPhoneNumber());
        
        //중복확인후 400반환
        if(!signUpService.presidentCheckEmail(president.getEmail())) {
            response.setStatus(409);
            return;
        }

        if(!signUpService.signUpPresident(president)) {
            response.setStatus(403);
        }
    }

    @GetMapping(value = "/major-list")
    public Map<String,Object> getMajorList() {
        return signUpService.getMajorList();
    }

}