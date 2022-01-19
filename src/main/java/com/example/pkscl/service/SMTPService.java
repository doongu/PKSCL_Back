package com.example.pkscl.service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.repository.PresidentRepository;
import com.example.pkscl.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class SMTPService {

    private final JavaMailSender mailSender;
    private final StudentRepository studentRepository;
    private final PresidentRepository presidentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SMTPService(JavaMailSender mailSender, StudentRepository studentRepository, PresidentRepository presidentRepository, PasswordEncoder passwordEncoder) {
        this.mailSender = mailSender;
        this.studentRepository = studentRepository;
        this.presidentRepository = presidentRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    private static final String SECRET_KEY = "tempSecretKey";

    public void sendEmail(String toEmail
            , String subject
            , String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("pkscl.official@gmail.com");
        message.setTo(toEmail); 
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public String generateToken(String content, long exptime){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setSubject(content)
                .signWith(signatureAlgorithm, signingKey)
                .setExpiration(new Date(System.currentTimeMillis()+exptime))
                .compact();
    }

    public String decodeToken(String token){
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Integer studentTempPassword(String email, String name, String studentId){

        // 이메일로 사용자 검색
        Optional<Student> student = studentRepository.findByEmail(email).stream().findFirst();
        if (!student.isPresent()) return -1;
        if (!student.get().getName().equals(name) || !student.get().getStudentid().equals(studentId)) return -2;

        // 임시 비밀번호 숫자 6자리로 생성
        String tempPassword = "PKSCL";
        for(int i=0; i<5; i++){
            tempPassword += (int)(Math.random()*10);
        }
        String subject = "임시 비밀번호 발급";
        String body = "임시 비밀번호는 " + tempPassword + " 입니다.";
    
        // 메일 전송
        sendEmail(email, subject, body);

        // 비밀번호 변경
        student.get().setPassword(passwordEncoder.encode(tempPassword));
        studentRepository.save(student.get());
        return 1;
    }
    
    public Integer presidentTempPassword(String email, String name, String studentId){

        // 이메일로 사용자 검색
        Optional<President> president = presidentRepository.findByEmail(email).stream().findFirst();
        if (!president.isPresent()) return -1;
        if (!president.get().getName().equals(name) || !president.get().getStudentid().equals(studentId)) return -2;

        // 임시 비밀번호 숫자 6자리로 생성
        String tempPassword = "PKSCL";
        for(int i=0; i<5; i++){
            tempPassword += (int)(Math.random()*10);
        }
        String subject = "임시 비밀번호 발급";
        String body = "임시 비밀번호는 " + tempPassword + " 입니다.";
    
        // 메일 전송
        sendEmail(email, subject, body);

        // 비밀번호 변경
        president.get().setPassword(passwordEncoder.encode(tempPassword));
        presidentRepository.save(president.get());
        return 1;
    }
}
