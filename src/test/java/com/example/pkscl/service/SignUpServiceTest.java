package com.example.pkscl.service;

import com.example.pkscl.domain.member.Student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SignUpServiceTest {
    
    @Autowired
    private SignUpService signUpService;

    @Test
    @Commit
    void 학생회원가입(){
        // Given
        Student student = new Student();
        student.setEmail("hello2@test.com");
        student.setMajornumber(123);
        student.setStudentid("12345");
        student.setPassword("1234");
        student.setName("hello");
        student.setCertfilepath("hello");
        student.setStatus("hello");

        // When
        signUpService.signUpStudent(student);
    }

}
