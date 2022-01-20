package com.example.pkscl.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.example.pkscl.service.SMTPService;
import com.example.pkscl.service.SignUpService;

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
    private final SignUpService signUpService;

    @Autowired
    public SMTPController(SMTPService smtpService, SignUpService signUpService) {
        this.smtpService = smtpService;
        this.signUpService = signUpService;
    }

    // 사용자로부터 값을 받아 토큰 생성후 반환
    @PostMapping(value = "/token")
    public ResponseEntity<String> getToken(@RequestParam Map<String, Object> param) {
        String email = (String) param.get("email");
        String token = smtpService.generateToken(email, (2*1000*60));
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    // 인증 메일 보내기
    @PostMapping(value = "/email/student")
    public ResponseEntity<Map<String,Object>> studentSendEmail(@RequestBody Map<String, Object> param) {

        String email = (String) param.get("email");
        // 중복확인
        if(!signUpService.studentCheckEmail(email)) {
            LinkedHashMap<String, Object> errorMsg = new LinkedHashMap<>();
            errorMsg.put("errorMessage", "이미 가입되어 있는 이메일입니다.");
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }
        smtpService.sendEmailAuth(email, "student");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/email/president")
    public ResponseEntity<Map<String,Object>> presidentSendEmail(@RequestBody Map<String, Object> param) {

        String email = (String) param.get("email");
        // 중복확인
        if(!signUpService.presidentCheckEmail(email)) {
            LinkedHashMap<String, Object> errorMsg = new LinkedHashMap<>();
            errorMsg.put("errorMessage", "이미 가입되어 있는 이메일입니다.");
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }
        smtpService.sendEmailAuth(email, "president");
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping(value = "/verify/token/student")
    public ResponseEntity<Void> studentVerifyToken(@RequestParam String token) {
        if(!smtpService.studentVerifyToken(token)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping(value = "/verify/token/president")
    public ResponseEntity<Void> presidentVerifyToken(@RequestParam String token) {
        if(!smtpService.presidentVerifyToken(token)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
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
