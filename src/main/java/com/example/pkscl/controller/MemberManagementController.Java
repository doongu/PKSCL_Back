package com.example.pkscl.controller;

import com.example.pkscl.service.MemberManagementService;
import com.example.pkscl.service.SignInService;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class MemberManagementController {

    private final MemberManagementService memberManagementService;

    @Autowired
    public MemberManagementController(MemberManagementService memberManagementService) {
        this.memberManagementService = memberManagementService;
    }

    @GetMapping(value = "/student-list")
    public ResponseEntity<LinkedHashMap<String,Object>> stduentList(HttpServletRequest request) {
        // Session에서 major num 가져옴
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");

        // 반환할 데이터 형식
        LinkedHashMap<String,Object> result = new LinkedHashMap<>();

        // Data 받아옴 (정렬된 데이터 받아옴
        result.put( "studentPresidentList",  memberManagementService.getStudentData(majorNumber));

        //DATA 리턴
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
