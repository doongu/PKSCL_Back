package com.example.pkscl.service;

import com.example.pkscl.domain.major.Major;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.repository.MajorRepository;
import com.example.pkscl.repository.StudentRepository;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.example.pkscl.repository.StudentRepository.*;

@Service
public class MemberManagementService {

    private StudentRepository studentRepository;

    @Autowired
    public MemberManagementService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public  LinkedHashMap<String, Object>  getStudentData(String majorNumber) {

        LinkedHashMap<String, Object> studentPresident = new LinkedHashMap<>();

        // 리스트 형식으로 받아옴
        List<Student> students= studentRepository.findByMajornumber(majorNumber);


        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        List<LinkedHashMap<String, Object>>waiting_list = new ArrayList<>();
        List<LinkedHashMap<String, Object>> refusal_list = new ArrayList<>();
        List<LinkedHashMap<String, Object>> approval_list = new ArrayList<>();

        // 로직 작성
        // 필요한 데이터만 추출
        for( int index = 0;  index < students.size(); index++) {

            String status = students.get(index).getStatus();

            String studentId = students.get(index).getStudentid();
            String name = students.get(index).getName();
            String certfilepath = students.get(index).getCertfilepath();
            LinkedHashMap<String, Object> tmp = new LinkedHashMap<>();
            tmp.put("studentId", studentId);
            tmp.put("studentId", name);
            tmp.put("studentId", certfilepath);

            if (status == "waiting"){

                waiting_list.add(tmp);
            }
            else if (status == "refusal"){
                refusal_list.add(tmp);
            }
            else if (status == "approval"){
                approval_list.add(tmp);
            }
        }




//
        result.put( "waiting", waiting_list );
        result.put( "refusal", waiting_list );
        result.put( "approval", waiting_list );

        // 리스트 반환, 이후 controller에서 json형식으로 반환.
        return result;
    }

}
