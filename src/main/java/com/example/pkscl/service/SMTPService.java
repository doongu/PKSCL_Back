package com.example.pkscl.service;

import java.security.Key;
import java.util.Date;

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

    // ????????? ????????? ??????
    public void sendEmailAuth(String toEmail, String position){
        // ?????? ??? url ??????
        String token = generateToken(toEmail, (2*1000*60));
        String subject = "???????????? ?????? ???????????????.";
        // ????????????
        String headInfo = "???????????????. PKSCL ??????????????????.\n\n" + "??????????????? ?????? ????????? ????????? ???????????????.\n\n";
        String url = "https://pkscl.kro.kr/verify/token/"+ position +"/?token=" + token;
        String tailInfo = "\n\n????????? ??????????????? ????????? ????????? ???????????????.\n\n" + "??? ????????? ???????????? ???????????????.\n\n";
        String body = headInfo + url + tailInfo;
        // ????????? ?????? DB ??????
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
        // ????????? ??????
        sendEmail(toEmail, subject, body);
    }

    // ?????? ??????
    public boolean studentVerifyToken(String token){
        try{
            StudentCertemail certemail = studentCertemailRepository.findByEmail(decodeToken(token));
            if(certemail == null){
                return false;
            }
            // status??? 1??? ??????
            certemail.setStatus(1);
            studentCertemailRepository.save(certemail);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public boolean presidentVerifyToken(String token){
        try{
            PresidentCertemail certemail = presidentCertemailRepository.findByEmail(decodeToken(token));
            if(certemail == null){
                return false;
            }
            // status??? 1??? ??????
            certemail.setStatus(1);
            presidentCertemailRepository.save(certemail);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public Integer studentTempPassword(String email, String name, String studentId){

        // ???????????? ????????? ??????
        Student student = studentRepository.findByEmail(email);
        if (student == null) return -1;
        if (!student.getName().equals(name) || !student.getStudentid().equals(studentId)) return -2;

        // ?????? ???????????? ?????? 6????????? ??????
        String tempPassword = "PKSCL";
        for(int i=0; i<5; i++){
            tempPassword += (int)(Math.random()*10);
        }
        String subject = "?????? ???????????? ??????";
        String body = "?????? ??????????????? " + tempPassword + " ?????????.";
    
        // ?????? ??????
        sendEmail(email, subject, body);

        // ???????????? ??????
        student.setPassword(passwordEncoder.encode(tempPassword));
        studentRepository.save(student);
        return 1;
    }
    
    public Integer presidentTempPassword(String email, String name, String studentId){

        // ???????????? ????????? ??????
        President president = presidentRepository.findByEmail(email);
        if (president == null) return -1;
        if (!president.getName().equals(name) || !president.getStudentid().equals(studentId)) return -2;

        // ?????? ???????????? ?????? 6????????? ??????
        String tempPassword = "PKSCL";
        for(int i=0; i<5; i++){
            tempPassword += (int)(Math.random()*10);
        }
        String subject = "?????? ???????????? ??????";
        String body = "?????? ??????????????? " + tempPassword + " ?????????.";
    
        // ?????? ??????
        sendEmail(email, subject, body);

        // ???????????? ??????
        president.setPassword(passwordEncoder.encode(tempPassword));
        presidentRepository.save(president);
        return 1;
    }

    public boolean checkEmailForm(String email) {
        // ????????? ?????? ??????
        //@pukyong.ac.kr ??? ????????? ????????? false
        if(!email.endsWith("@pukyong.ac.kr")) return false;
        return true;
    }
}
