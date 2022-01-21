package com.example.pkscl.domain.member;

import lombok.Data;

@Data
public class StudentModel {
    private String email;
    private String password;
    private String checkPassword;
    private String name;
    private int major;
    private String stdID;
}
