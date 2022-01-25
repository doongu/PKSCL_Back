package com.example.pkscl.controller;

import com.example.pkscl.service.MemberManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
public class MemberManagementController {

    private final MemberManagementService memberManagementService;

    @Autowired
    public MemberManagementController(MemberManagementService memberManagementService) {
        this.memberManagementService = memberManagementService;
    }

    @PatchMapping(value = "/student-list")
    public void patchStudentStatus(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {

        // 서비스 파라미터 설정
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String patchStatus = (String) body.get("status");
        List<String> emailList = (List<String>) body.get("email");

        // 400 Bad Request
        if(emailList == null || emailList.size() == 0 || patchStatus == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 서비스 호출
        for(String email : emailList) {
            memberManagementService.patchStudentStatus(email, patchStatus, majorNumber);
        }
    }
    
    @GetMapping(value = "/student-list")
    public Map<String,Object> studentList(HttpServletRequest request, HttpServletResponse response) {

        // 서비스 파라미터 설정
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");

        // 400 Bad Request
        if(majorNumber == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        // 서비스 호출 및 반환
        return memberManagementService.getStudentData(majorNumber);
    }


}
