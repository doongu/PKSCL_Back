package com.example.pkscl.domain.member;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;


@Data
@Entity
public class President {

    @Id
    private String email;
    private int majornumber;
    private String studentid;
    private String password;
    private String name;
    private String phonenumber;
    private String majorlogo;
    private String status;
}
