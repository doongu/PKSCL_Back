package com.example.pkscl.controller;

import com.example.pkscl.service.MemberManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;

@RestController
public class MemberManagementController {

    private final MemberManagementService memberManagementService;

    @Autowired
    public MemberManagementController(MemberManagementService memberManagementService) {
        this.memberManagementService = memberManagementService;
    }
    
    @GetMapping(value = "/student-list")
    public ResponseEntity<LinkedHashMap<String,Object>> studentList(HttpServletRequest request) {
        // Session에서 major num 가져옴
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        // DATA 리턴
        return new ResponseEntity<>(memberManagementService.getStudentData(majorNumber),HttpStatus.OK);
    }


}
