package com.example.pkscl.controller;

import java.util.Map;

import com.example.pkscl.service.SMTPService;

import org.hibernate.engine.query.spi.ReturnMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SMTPController {
    
    private final SMTPService smtpService;

    @Autowired
    public SMTPController(SMTPService smtpService) {
        this.smtpService = smtpService;
    }

    // // 사용자로부터 값을 받아 토큰 생성후 반환
    // @PostMapping(value = "/token")
    // public ResponseEntity<String> getToken(@RequestParam Map<String, Object> param) {
    //     String email = (String) param.get("email");
    //     String token = smtpService.generateToken(email, (2*1000*60));
    //     return new ResponseEntity<>(token, HttpStatus.OK);
    // }

    @PostMapping(value = "/email")
    public ResponseEntity<Void> sendEmail(@RequestBody Map<String, Object> param) {

        String email = (String) param.get("email");
        String token = smtpService.generateToken(email, (2*1000*60));
        String subject = "JWT 테스트";
        String body = "<a href='http://localhost:8080/verify/token?token=" + token + "'></a>";

        if(email == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        smtpService.sendEmail(email, subject, body);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/verify/token")
    public ResponseEntity<String> verifyToken(@RequestParam String token) {
        String result = smtpService.decodeToken(token);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    // 임시 비밀번호 발급
    @PostMapping(value = "/newpwd/student")
    public ResponseEntity<Void> studentNewPwd(@RequestBody Map<String, Object> param) {
        String email = (String) param.get("email");
        String name = (String) param.get("name");
        String studentId = (String) param.get("stdID");
        if(smtpService.studentTempPassword(email, name, studentId) < 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/newpwd/president")
    public ResponseEntity<Void> presidentNewPwd(@RequestBody Map<String, Object> param) {
        String email = (String) param.get("email");
        String name = (String) param.get("name");
        String studentId = (String) param.get("stdID");
        if(smtpService.presidentTempPassword(email, name, studentId) < 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
