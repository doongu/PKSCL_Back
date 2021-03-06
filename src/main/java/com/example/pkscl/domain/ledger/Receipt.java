package com.example.pkscl.domain.ledger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Receipt {
    @Id     
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int receiptnumber;
    int eventnumber;
    String receipttitle;
    String receiptimg;
    String receiptcontext;
}