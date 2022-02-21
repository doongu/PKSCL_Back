package com.example.pkscl.controller;

import com.example.pkscl.domain.member.*;
import com.example.pkscl.service.ProfileService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class ProfileController {
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProfileController(ProfileService profileService, PasswordEncoder passwordEncoder) {
        this.profileService = profileService;
        this.passwordEncoder = passwordEncoder;
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
    @PutMapping(value = "/profile/student") //president로 나눠야함 form양식이 달라서
    public void patchStudentStatus(@ModelAttribute StudentProfileModel studentProfileModel, MultipartFile certFile,  HttpServletRequest request, HttpServletResponse response) throws Exception{

        // 403
        if(studentProfileModel.getStdID().equals(null) || studentProfileModel.getMajor() == 0 ||
            studentProfileModel.getName().equals(null)) {
            Map<String,Object> errorMsg = new LinkedHashMap<>();
            response.setStatus(403);
            return;
        }

        // 세션서 이메일값을 받아온다.
        String email = (String) request.getSession(false).getAttribute("email");

        String stdID = studentProfileModel.getStdID();
        int major =  studentProfileModel.getMajor();
        String name  = studentProfileModel.getName();

        String fileName = null;

        if(certFile != null) {
            fileName = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
            fileName = fileName + certFile.getOriginalFilename().substring(certFile.getOriginalFilename().lastIndexOf("."));
            profileService.fileUploadStd(fileName, certFile);

        }

        // 레포에 업데이트
        profileService.putStudentProfileData(email, stdID, major, name, fileName);


    }

    @PutMapping(value = "/profile/president") //president로 나눠야함 form양식이 달라서
    public void patchPresidentStatus(@ModelAttribute PresidentProfileModel presidentProfileModel, MultipartFile majorLogo,  HttpServletRequest request, HttpServletResponse response) throws Exception{

        // 403 Forbidden
        if(presidentProfileModel.getStdID().equals(null) || presidentProfileModel.getPhoneNumber().equals(null) ||
            presidentProfileModel.getName().equals(null)) {
            response.setStatus(403);
            return;
        }

        String email = (String) request.getSession(false).getAttribute("email");

        String stdID = presidentProfileModel.getStdID();
        String name = presidentProfileModel.getName();
        String phoneNumber= presidentProfileModel.getPhoneNumber();


        String fileName = null;

        if(majorLogo != null) {
            fileName = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
            fileName = fileName + majorLogo.getOriginalFilename().substring(majorLogo.getOriginalFilename().lastIndexOf("."));
            profileService.fileUploadLogo(fileName, majorLogo);

        }
        profileService.putPresidentProfileData(email, stdID, name, phoneNumber, fileName);
    }

    @PatchMapping(value = "/password")
    public void patchPassword(@RequestBody Map<String, Object> body,  HttpServletRequest request, HttpServletResponse response){

        String email =(String) request.getSession(false).getAttribute("email");
        String position = (String) request.getSession(false).getAttribute("position");
        
        String inputPassword = (String) body.get("inputPassword");
        String inputNewPassword = (String) body.get("inputNewPassword");
        String inputCheckNewPassword = (String) body.get("inputCheckNewPassword");

        // 403 Forbidden
        if(inputPassword.equals(null) || inputNewPassword .equals(null)||  !inputNewPassword.equals(inputCheckNewPassword)) {
            response.setStatus(403); return;
        }

        else if(position.equals("student")){
        // 학생 기존 비번이랑 같은지 체크
            if (!passwordEncoder.matches(inputPassword, profileService.getStudentPassword(email))) {
                response.setStatus(401); return;
            }
            else profileService.patchStudentPassword(email, inputNewPassword);
        }

        else if(position.equals("president")){
        // 학생 기존 비번이랑 같은지 체크
            if(!passwordEncoder.matches(inputPassword, profileService.getPresidentPassword(email))) {
                response.setStatus(401); return;
            }
            else profileService.patchPresidentPassword(email, inputNewPassword);
        }

 }
}