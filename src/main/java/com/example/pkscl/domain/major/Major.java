package com.example.pkscl.domain.major;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Major {

    @Id
    private String majornumber;
    private String majorname;
    private String majorstatus;
    // private String majorlogo;
    private String name;
    private String phonenumber;
    private String email;

}
