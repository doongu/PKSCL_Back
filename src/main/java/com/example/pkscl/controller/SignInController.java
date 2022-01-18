package com.example.pkscl.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.example.pkscl.service.SignInService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignInController {

    private final SignInService passwordService;
    
    @Autowired  
    public SignInController(SignInService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping(value = "/signin/student")
    public ResponseEntity<LinkedHashMap<String, Object>> studentSignIn(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        String password = (String) body.get("password");

        if (email == null || password == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean match = passwordService.studentMatch(password, email);

        if(!match) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // 임의로 JSON 데이터 생성
        LinkedHashMap<String, Object> studentPresident = new LinkedHashMap<>();
        studentPresident.put("major", "컴퓨터공학과");
        studentPresident.put("name", "홍길동");
        studentPresident.put("phoneNumber", "01012345678");
        studentPresident.put("email", "test@naver.com");
        LinkedHashMap<String, Object> quarter = new LinkedHashMap<>();
        LinkedHashMap<String, Object> sclData = new LinkedHashMap<>();
        sclData.put("studentPresident", studentPresident);
        sclData.put("quater", quarter);
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("position", "student");
        result.put("sclData", sclData);
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/signin/president")
    public ResponseEntity<LinkedHashMap<String, Object>> presidentSignIn(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        String password = (String) body.get("password");

        if (email == null || password == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean match = passwordService.presidentMatch(password, email);

        if(!match) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // 임의로 JSON 데이터 생성
        LinkedHashMap<String, Object> studentPresident = new LinkedHashMap<>();
        studentPresident.put("major", "컴퓨터공학과");
        studentPresident.put("name", "홍길동");
        studentPresident.put("phoneNumber", "01012345678");
        studentPresident.put("email", "test@naver.com");
        LinkedHashMap<String, Object> quarter = new LinkedHashMap<>();
        LinkedHashMap<String, Object> sclData = new LinkedHashMap<>();
        sclData.put("studentPresident", studentPresident);
        sclData.put("quater", quarter);
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("position", "president");
        result.put("sclData", sclData);
        
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PostMapping(value = "/signin/admin")
    public ResponseEntity<LinkedHashMap<String, Object>> adminSignIn(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        String password = (String) body.get("password");

        if (id == null || password == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean match = passwordService.adminMatch(password, id);

        if(!match) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
