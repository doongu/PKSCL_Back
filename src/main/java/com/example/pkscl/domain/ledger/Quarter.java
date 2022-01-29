package com.example.pkscl.domain.ledger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.Data;

@Entity
@Data
@IdClass(QuarterKey.class)
public class Quarter {
    @Id
    int majornumber;
    @Id
    String quarternumber;
    String opendate;
    String enddate;
}
