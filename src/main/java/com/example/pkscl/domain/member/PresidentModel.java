package com.example.pkscl.domain.member;

import lombok.Data;

@Data
public class PresidentModel {
    private String email;
    private String password;
    private String checkPassword;
    private String name;
    private int major;
    private String stdID;
    private String phoneNumber;
}