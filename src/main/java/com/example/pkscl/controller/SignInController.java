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
    public void studentSignIn(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
        
        // 서비스 파라미터 설정
        String email = (String) body.get("email");
        String password = (String) body.get("password");

        // 400 Bad Request
        if(email == null || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 401 Unauthorized
        boolean match = signInService.studentMatch(password, email);
        if(!match) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // 세션 설정
        String majorNumber = signInService.getStudentMajor(email)+"";
        String status = signInService.getStudentStatus(email);
        HttpSession session = request.getSession();
        session.setAttribute("position", "student");
        session.setAttribute("email", email);
        session.setAttribute("majorNumber", majorNumber);
        session.setAttribute("status", status);
        
    }

    @PostMapping(value = "/login/president")
    public void presidentSignIn(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
    
        // 서비스 파라미터 설정
        String email = (String) body.get("email");
        String password = (String) body.get("password");

        // 400 Bad Request
        if (email == null || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 401 Unauthorized
        boolean match = signInService.presidentMatch(password, email);
        if(!match) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 세션 생성
        String majorNumber = signInService.getPresidentMajor(email)+"";
        String status = signInService.getPresidentStatus(email);
        HttpSession session = request.getSession();
        session.setAttribute("position", "president");
        session.setAttribute("email", email);
        session.setAttribute("majorNumber", majorNumber);
        session.setAttribute("status", status);
            
    }

    @PostMapping(value = "/login/admin")
    public void adminSignIn(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
        
        // 서비스 파라미터 설정
        String id = (String) body.get("id");
        String password = (String) body.get("password");

        // 400 bad request
        if (id == null || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 401 unauthorized
        boolean match = signInService.adminMatch(password, id);
        if(!match){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 세션 생성
        HttpSession session = request.getSession();
        session.setAttribute("position", "admin");
        session.setAttribute("id", id);

    }


    @PostMapping("/logout") 
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException
    { 
        HttpSession session = request.getSession(false);
        session.invalidate();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @PostMapping("/withdrawal")
    public void secession(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // 서비스 파라미터 설정
        HttpSession session = request.getSession(false);
        String position = (String) session.getAttribute("position");
        String email = (String) session.getAttribute("email");
        String checkemail = (String) body.get("email");
        String password = (String) body.get("password");

        // 400 bad request
        if (checkemail == null || password == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;

        }

        if(position.equals("student")) {

            // 401 unauthorized
            // 이메일, 비밀번호 일치 여부 확인
            if(!checkemail.equals(email) || !signInService.studentMatch(password, email)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 탈퇴 
            signInService.withdrawalStudent(email);

        } else if(position.equals("president")) {

            // 401 unauthorized
            // 이메일, 비밀번호 일치 여부 확인
            if(!checkemail.equals(email) || !signInService.presidentMatch(password, email)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 탈퇴
            signInService.withdrawalPresident(email);

        }

        // 세션 삭제
        session.invalidate();
        response.setStatus(HttpServletResponse.SC_OK);

    }
}
