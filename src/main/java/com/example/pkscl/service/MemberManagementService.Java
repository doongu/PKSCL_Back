package com.example.pkscl.service;

import com.example.pkscl.domain.member.Student;
import com.example.pkscl.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@Service
public class MemberManagementService {

    private StudentRepository studentRepository;

    @Autowired
    public MemberManagementService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public LinkedHashMap<String, Object> getStudentData(String majorNumber) {

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        // 리스트 형식으로 받아옴
        List<Student> students= studentRepository.findByMajornumber(Integer.parseInt(majorNumber));

        LinkedHashMap<String, Object> studentPresident = new LinkedHashMap<>();
        List<LinkedHashMap<String, Object>> waiting_list = new ArrayList<>();
        List<LinkedHashMap<String, Object>> refusal_list = new ArrayList<>();
        List<LinkedHashMap<String, Object>> approval_list = new ArrayList<>();
        
        // 로직 작성
        // 필요한 데이터만 추출
        for(Student student : students) {
            String status = student.getStatus();
            String studentId = student.getStudentid();
            String name = student.getName();
            String email = student.getEmail();
            String certfilepath = student.getCertfilepath();

            LinkedHashMap<String, Object> studentInfo = new LinkedHashMap<>();
            studentInfo.put("studentId", studentId);
            studentInfo.put("name", name);
            studentInfo.put("email", email);
            studentInfo.put("studentImgPath", certfilepath);

            if(status.equals("waiting")) {
                    
                waiting_list.add(studentInfo);
            }
            else if (status.equals("refusal")) {
                refusal_list.add(studentInfo);
            }
            else if (status.equals("approval")) {
                approval_list.add(studentInfo);
            }
        }

        studentPresident.put("waiting", waiting_list );
        studentPresident.put("refusal", refusal_list );
        studentPresident.put("approval", approval_list );

        result.put("studentPresident", studentPresident);
        return result;
    }

}
