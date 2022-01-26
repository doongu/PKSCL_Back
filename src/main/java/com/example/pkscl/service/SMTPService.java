package com.example.pkscl.service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.example.pkscl.domain.member.President;
import com.example.pkscl.domain.member.Student;
import com.example.pkscl.domain.temp.PresidentCertemail;
import com.example.pkscl.domain.temp.StudentCertemail;
import com.example.pkscl.repository.StudentCertemailRepository;
import com.example.pkscl.repository.PresidentCertemailRepository;
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
    private final StudentCertemailRepository studentCertemailRepository;
    private final PresidentCertemailRepository presidentCertemailRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public SMTPService(JavaMailSender mailSender, StudentRepository studentRepository, PresidentRepository presidentRepository, StudentCertemailRepository studentCertemailRepository, PresidentCertemailRepository presidentCertemailRepository, PasswordEncoder passwordEncoder) {
        this.mailSender = mailSender;
        this.studentRepository = studentRepository;
        this.presidentRepository = presidentRepository;
        this.studentCertemailRepository = studentCertemailRepository;
        this.presidentCertemailRepository = presidentCertemailRepository;
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

    // 인증용 이메일 발송
    public void sendEmailAuth(String toEmail, String position){
        // 토큰 및 url 생성
        String token = generateToken(toEmail, (2*1000*60));
        String subject = "회원가입 인증 메일입니다.";
        String body = "https://pkscl.kro.kr/verify/token/"+ position +"/?token=" + token;

        // 이메일 정보 DB 저장
        if(position.equals("student")){
            StudentCertemail studentCertemail = new StudentCertemail();
            studentCertemail.setEmail(toEmail);
            studentCertemailRepository.save(studentCertemail);
        }
        else if(position.equals("president")){
            PresidentCertemail presidentCertemail = new PresidentCertemail();
            presidentCertemail.setEmail(toEmail);
            presidentCertemailRepository.save(presidentCertemail);
        }
        // 이메일 발송
        sendEmail(toEmail, subject, body);
    }

    // 토큰 인증
    public boolean studentVerifyToken(String token){
        StudentCertemail certemail = studentCertemailRepository.findByEmail(decodeToken(token));
        if(certemail == null){
            return false;
        }
        // status를 1로 변경
        certemail.setStatus(1);
        studentCertemailRepository.save(certemail);
        return true;
    }

    public boolean presidentVerifyToken(String token){
        PresidentCertemail certemail = presidentCertemailRepository.findByEmail(decodeToken(token));
        if(certemail == null){
            return false;
        }
        // status를 1로 변경
        certemail.setStatus(1);
        presidentCertemailRepository.save(certemail);
        return true;
    }

    public Integer studentTempPassword(String email, String name, String studentId){

        // 이메일로 사용자 검색
        Student student = studentRepository.findByEmail(email);
        if (student == null) return -1;
        if (!student.getName().equals(name) || !student.getStudentid().equals(studentId)) return -2;

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
        student.setPassword(passwordEncoder.encode(tempPassword));
        studentRepository.save(student);
        return 1;
    }
    
    public Integer presidentTempPassword(String email, String name, String studentId){

        // 이메일로 사용자 검색
        President president = presidentRepository.findByEmail(email);
        if (president == null) return -1;
        if (!president.getName().equals(name) || !president.getStudentid().equals(studentId)) return -2;

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
        president.setPassword(passwordEncoder.encode(tempPassword));
        presidentRepository.save(president);
        return 1;
    }
}
