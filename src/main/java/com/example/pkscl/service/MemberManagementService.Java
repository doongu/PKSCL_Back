package com.example.pkscl.service;

import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.repository.MajorRepository;
import com.example.pkscl.repository.PresidentRepository;
import com.example.pkscl.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@Service
public class MemberManagementService {

    private StudentRepository studentRepository;
    private PresidentRepository presidentRepository;
    private MajorRepository majorRepository;

    @Autowired
    public MemberManagementService(StudentRepository studentRepository, PresidentRepository presidentRepository, MajorRepository majorRepository) {
        this.studentRepository = studentRepository;
        this.presidentRepository = presidentRepository;
        this.majorRepository = majorRepository;
    }


    public LinkedHashMap<String, Object> getStudentData(String majorNumber) {

        // 학생을 리스트 형식으로 받아옴
        List<Student> students= studentRepository.findByMajornumber(Integer.parseInt(majorNumber));

        LinkedHashMap<String, Object> studentList = new LinkedHashMap<>();
        List<LinkedHashMap<String, Object>> waitingList = new ArrayList<>();
        List<LinkedHashMap<String, Object>> refusalList = new ArrayList<>();
        List<LinkedHashMap<String, Object>> approvalList = new ArrayList<>();
        
        // 로직 작성
        // 필요한 데이터만 추출
        for(Student student : students) {
            String status = student.getStatus();
            String studentId = student.getStudentid();
            String name = student.getName();
            String email = student.getEmail();
            String certfilepath = student.getCertfilepath();

            LinkedHashMap<String, Object> studentInfo = new LinkedHashMap<>();
            studentInfo.put("stdID", studentId);
            studentInfo.put("name", name);
            studentInfo.put("email", email);
            studentInfo.put("studentImgPath", certfilepath);

            if(status.equals("waiting")) {
                    
                waitingList.add(studentInfo);
            }
            else if (status.equals("refusal")) {
                refusalList.add(studentInfo);
            }
            else if (status.equals("approval")) {
                approvalList.add(studentInfo);
            }
        }

        studentList.put("waiting", waitingList );
        studentList.put("refusal", refusalList );
        studentList.put("approval", approvalList );

        return studentList;
    }

    public void patchStudentStatus(String email, String patchStatus, String majorNumber) {
        Student student = studentRepository.findByEmail(email);
        if(student.getMajornumber() == Integer.parseInt(majorNumber)) {
            student.setStatus(patchStatus);
            studentRepository.save(student);
        }
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
            String studentImgPath = president.getMajorlogo();

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