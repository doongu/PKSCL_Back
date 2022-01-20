package com.example.pkscl.domain.temp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "president_certemail")
public class PresidentCertemail {
    @Id
    private String email;
    private int status;
}
