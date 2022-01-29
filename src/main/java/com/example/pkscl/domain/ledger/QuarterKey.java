package com.example.pkscl.domain.ledger;

import java.io.Serializable;

import lombok.Data;

@Data
public class QuarterKey implements Serializable {
    int majornumber;
    String quarternumber;
}
