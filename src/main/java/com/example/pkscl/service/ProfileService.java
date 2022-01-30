package com.example.pkscl.service;

import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.repository.PresidentRepository;
import com.example.pkscl.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@Service
public class ProfileService {

    private StudentRepository studentRepository;
    private PresidentRepository presidentRepository;


    @Autowired
    public ProfileService(StudentRepository studentRepository, PresidentRepository presidentRepository) {
        this.studentRepository = studentRepository;
        this.presidentRepository = presidentRepository;
    }


    public LinkedHashMap<String, Object> getProfileData(String position,String email,String majorNumber) {


        


        // 학생을 리스트 형식으로 받아옴
        if(position == "student") {
            Student profileData = studentRepository.findByEmail(email);

            String studentId = profileData.getStudentid();
            String pMajorNumber = profileData.getMajorNumber();
            Major major = majorRepository.findByMajornumber(majorNumber);
            String majorName = major.getMajorname();
            String name = profileData.getName();
            String pEmail = profileData.getEmail();
            String certfilepath = profileData.getCertfilepath();

            LinkedHashMap<String, Object> profileInfo = new LinkedHashMap<>();

            profileInfo.put("stdID", studentId);
            profileInfo.put("major", pMajorNumber);
            profileInfo.put("name", name);
            profileInfo.put("email", pEmail);
            profileInfo.put("stdPath", certfilepath);


        }

        else if(position == "president"){
            List<Student> profileData = presidentRepository.findByEmail(email);

            String studentId = profileData.getStudentid();
            String pMajorNumber = profileData.getMajorNumber();
            String name = profileData.getName();
            String phoneNumber = profileData.getPhoneNumber();
            String pEmail = profileData.getEmail();
            String certfilepath = profileData.getCertfilepath();

            LinkedHashMap<String, Object> profileInfo = new LinkedHashMap<>();

            profileInfo.put("stdID", studentId);
            profileInfo.put("major", pMajorNumber);
            profileInfo.put("name", name);
            profileInfo.put("phoneNumber", phoneNumber);
            profileInfo.put("email", pEmail);
            profileInfo.put("studentImgPath", certfilepath);
            profileInfo.put("majorLogo", "nothing");
            

        }


        return profileInfo;
    }

    public LinkedHashMap<String, Object> getPresidentData() {
        
        // 학과회장을 리스트 형식으로 받아옴
        List<President> presidents = presidentRepository.findAll();

        LinkedHashMap<String, Object> presidentList = new LinkedHashMap<>();
        List<LinkedHashMap<String, Object>> waitingList = new ArrayList<>();
        List<LinkedHashMap<String, Object>> refusalList = new ArrayList<>();
        List<LinkedHashMap<String, Object>> approvalList = new ArrayList<>();

        for(President president : presidents) {
            String status = president.getStatus();
            String majorNumber = String.valueOf(president.getMajornumber());
            String majorName = majorRepository.findByMajornumber(majorNumber).getMajorname();
            String email = president.getEmail();
            String stdID = president.getStudentid();
            String name = president.getName();
            String phoneNumber = president.getPhonenumber();
            String studentImgPath = president.getCertfilepath();

            LinkedHashMap<String, Object> presidentInfo = new LinkedHashMap<>();
            presidentInfo.put("major", majorName);
            presidentInfo.put("email", email);
            presidentInfo.put("stdID", stdID);
            presidentInfo.put("name", name);
            presidentInfo.put("phoneNumber", phoneNumber);
            presidentInfo.put("studentImgPath", studentImgPath);

            if(status.equals("waiting")) {
                waitingList.add(presidentInfo);
            }
            else if (status.equals("refusal")) {
                refusalList.add(presidentInfo);
            }
            else if (status.equals("approval")) {
                approvalList.add(presidentInfo);
            }
        }

        presidentList.put("waiting", waitingList );
        presidentList.put("refusal", refusalList );
        presidentList.put("approval", approvalList );

        return presidentList;
        
    }

    public void patchPresidentStatus(String email, String patchStatus) {
        President president = presidentRepository.findByEmail(email);
        president.setStatus(patchStatus);
        presidentRepository.save(president);
    }
    
}
