package com.example.pkscl.domain.ledger;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Receiptdetail {
    @Id
    int detailnumber;
    int receiptnumber;
    String context;
    String price;
    String amount;
}