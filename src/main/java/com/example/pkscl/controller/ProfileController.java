package com.example.pkscl.controller;

import com.example.pkscl.service.ProfileService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class ProfileController {
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // 학생 및 학과회장 정보로드 
    @GetMapping(value = "/profile")
    public Map<String,Object> studentProfile(HttpServletRequest request, HttpServletResponse response) {

 

        // 세션 여부를 판단하기 위한 변수 설정
        String email = (String) request.getSession(false).getAttribute("email");
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String position = (String) request.getSession(false).getAttribute("position");



        // 400 Bad Request
        if(majorNumber == null || email==null || position==null) {
            Map<String,Object> errorMsg = new LinkedHashMap<>();
            errorMsg.put("errorMessage", "존재하지 않는 회원입니다.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return errorMsg;
        }

        // 서비스 호출 및 반환
        return profileService.getProfileData(position, email, majorNumber);
    }

    // 학생 정보 변경
//    @PatchMapping(value = "/profile")
//    public void patchStudentStatus(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
//
//        // 서비스 파라미터 설정
//        String position = (String) request.getSession(false).getAttribute("position");
//        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
//        String patchStatus = (String) body.get("status");
//        List<String> emailList = (List<String>) body.get("email");
//
//        // 400 Bad Request
//        if(emailList == null || emailList.size() == 0 || patchStatus == null) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        // 403 Forbidden
//        if(!position.equals("president")) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }
//
//        // 서비스 호출
//        for(String email : emailList) {
//            memberManagementService.patchStudentStatus(email, patchStatus, majorNumber);
//        }
//    }

}