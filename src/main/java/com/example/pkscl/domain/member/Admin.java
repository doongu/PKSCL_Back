package com.example.pkscl.domain.member;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Admin {
    @Id
    private String id;
    private String password;
}
