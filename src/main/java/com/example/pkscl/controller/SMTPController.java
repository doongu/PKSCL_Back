package com.example.pkscl.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.example.pkscl.service.SMTPService;
import com.example.pkscl.service.SignUpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SMTPController {
    
    private final SMTPService smtpService;
    private final SignUpService signUpService;

    @Autowired
    public SMTPController(SMTPService smtpService, SignUpService signUpService) {
        this.smtpService = smtpService;
        this.signUpService = signUpService;
    }

    @PostMapping(value = "/email/{position}")
    public void sendEmail(@RequestBody Map<String, Object> body, @PathVariable String position, HttpServletResponse response) {

        String email = (String) body.get("email");
        // 중복확인

        if(position.equals("student")){
            if(!signUpService.studentCheckEmail(email)) {
                response.setStatus(HttpStatus.CONFLICT.value());
                return;
            }
        }else if(position.equals("president")){
            if(!signUpService.presidentCheckEmail(email)) {
                response.setStatus(HttpStatus.CONFLICT.value());
                return;
            }
        }else{
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        smtpService.sendEmailAuth(email, position);
    }

    @GetMapping(value = "/verify/token/{position}")
    public void verifyToken(@RequestParam String token, @PathVariable String position, HttpServletResponse response) {
        if(position.equals("student")){
            if(!smtpService.studentVerifyToken(token)){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }
        }else if(position.equals("president")){
            if(!smtpService.presidentVerifyToken(token)){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }
        }else{
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }
    
    // 임시 비밀번호 발급
    @PostMapping(value = "/newpwd/{position}")
    public void newPassword(@RequestBody Map<String, Object> param, @PathVariable String position, HttpServletResponse response) {
        String email = (String) param.get("email");
        String name = (String) param.get("name");
        String studentId = (String) param.get("stdID");
        if(position.equals("student")){
            if(smtpService.studentTempPassword(email, name, studentId) < 0) response.setStatus(HttpStatus.BAD_REQUEST.value());
        }else if(position.equals("president")){
            if(smtpService.presidentTempPassword(email, name, studentId) < 0) response.setStatus(HttpStatus.BAD_REQUEST.value());
        }else{
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }
    
}
