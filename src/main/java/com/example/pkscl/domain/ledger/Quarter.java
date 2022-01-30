package com.example.pkscl.domain.ledger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.Data;

@Entity
@Data
public class Quarter {
    @Id
    int quarterid;
    int majornumber;
    String quarternumber;
    String opendate;
    String closedate;
}
