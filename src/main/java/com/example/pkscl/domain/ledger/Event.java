package com.example.pkscl.domain.ledger;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Event {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int eventnumber;
    int quarterid;
    String eventtitle;
    String eventcontext;
}
