package com.example.pkscl.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.pkscl.service.SignInService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignInController {

    private final SignInService signInService;
    
    @Autowired  
    public SignInController(SignInService signInService) {
        this.signInService = signInService;
    }

    @PostMapping(value = "/login/student")
    public ResponseEntity<Void> studentSignIn(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String email = (String) body.get("email");
        String password = (String) body.get("password");


        if (email == null || password == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean match = signInService.studentMatch(password, email);

        if(!match) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        
        // 세션
        String majorNumber = signInService.getStudentMajor(email)+"";
        String status = signInService.getStudentStatus(email);
        HttpSession session = request.getSession();
        session.setAttribute("position", "student");
        session.setAttribute("email", email);
        session.setAttribute("majorNumber", majorNumber);
        session.setAttribute("status", status);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/login/president")
    public ResponseEntity<Void> presidentSignIn(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String email = (String) body.get("email");
        String password = (String) body.get("password");

        if (email == null || password == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean match = signInService.presidentMatch(password, email);

        if(!match) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // 세션
        String majorNumber = signInService.getPresidentMajor(email)+"";
        String status = signInService.getPresidentStatus(email);
        HttpSession session = request.getSession();
        session.setAttribute("position", "president");
        session.setAttribute("email", email);
        session.setAttribute("majorNumber", majorNumber);
        session.setAttribute("status", status);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/login/admin")
    public ResponseEntity<LinkedHashMap<String, Object>> adminSignIn(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String id = (String) body.get("id");
        String password = (String) body.get("password");

        if (id == null || password == null) 
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean match = signInService.adminMatch(password, id);

        if(!match) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        HttpSession session = request.getSession();
        session.setAttribute("position", "admin");
        session.setAttribute("id", id);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/logout") 
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException
    { 
        HttpSession session = request.getSession(false);
        session.invalidate();
        response.sendRedirect("/");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
