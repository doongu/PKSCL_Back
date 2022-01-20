package com.example.pkscl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String,Object>> signUpStudent(@ModelAttribute StudentModel studentModel, MultipartFile certFile) throws Exception {

        Student student = new Student();
        student.setEmail(studentModel.getEmail());
        student.setPassword(studentModel.getPassword());
        student.setMajornumber(studentModel.getMajor());
        student.setStudentid(studentModel.getStdID());
        student.setName(studentModel.getName());
        
        // file_name을 현재시간을 기준으로 yyyyMMddHHmmssSSS.jpg 형태로 설정
        String filename = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
        String ext = certFile.getOriginalFilename().substring(certFile.getOriginalFilename().lastIndexOf("."));
        student.setCertfilepath(filename+ext);

        //중복확인후 400반환
        if(!signUpService.studentCheckEmail(student.getEmail())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        signUpService.fileUpload(filename+ext, certFile);
        if(!signUpService.signUpStudent(student)) {
            Map<String,Object> errorMsg = new LinkedHashMap<>();
            errorMsg.put("errorMessage", "이메일 인증이 완료되지 않았습니다.");
            return new ResponseEntity<>(errorMsg,HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/signup/president")
    public ResponseEntity<Map<String,Object>> signUpPresident(@ModelAttribute PresidentModel presidentModel, MultipartFile certFile) throws Exception {

        President president = new President();
        president.setEmail(presidentModel.getEmail());
        president.setPassword(presidentModel.getPassword());
        president.setName(presidentModel.getName());
        president.setMajornumber(presidentModel.getMajor());
        president.setStudentid(presidentModel.getStdID());
        president.setPhonenumber(presidentModel.getPhoneNumber());

        // file_name을 현재시간을 기준으로 yyyyMMddHHmmssSSS.jpg 형태로 설정
        String filename = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
        String ext = certFile.getOriginalFilename().substring(certFile.getOriginalFilename().lastIndexOf("."));
        president.setCertfilepath(filename+ext);
        
        //중복확인후 400반환
        if(!signUpService.presidentCheckEmail(president.getEmail())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        signUpService.fileUpload(filename+ext,certFile);
        if(!signUpService.signUpPresident(president)) {
            Map<String,Object> errorMsg = new LinkedHashMap<>();
            errorMsg.put("errorMessage", "이메일 인증이 완료되지 않았습니다.");
            return new ResponseEntity<>(errorMsg,HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/major-list")
    public ResponseEntity<LinkedHashMap<String,Object>> getMajorList() {
        LinkedHashMap<String,Object> result = new LinkedHashMap<>();
        result.put("majorList", signUpService.getMajorList());
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

}