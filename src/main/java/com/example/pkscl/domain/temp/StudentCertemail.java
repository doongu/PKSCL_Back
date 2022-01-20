package com.example.pkscl.domain.temp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "student_certemail")
public class StudentCertemail {
    @Id
    private String email;
    private int status;
}
