package com.example.pkscl.domain.ledger;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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