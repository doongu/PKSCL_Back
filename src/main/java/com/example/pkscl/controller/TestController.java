package com.example.pkscl.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/whoami")
    // 세션으로부터 email, position 정보를 가져온다.
    public String whoami(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        String position = (String) session.getAttribute("position");
        String majorNumber = (String) session.getAttribute("majorNumber");
        String status = (String) session.getAttribute("status");
        return "email: " + email + ", position: " + position + ", majorNumber: " + majorNumber + ", status: " + status;
    }
}
