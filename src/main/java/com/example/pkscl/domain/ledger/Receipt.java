package com.example.pkscl.domain.ledger;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Receipt {
    @Id
    int receiptNumber;
    int eventNumber;
    String receiptTitle;
    String receiptImg;
    String receiptContext;
}