package com.example.pkscl.domain.ledger;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Event {
    @Id
    int eventNumber;
    int majorNumber;
    String quarterNumber;
    String eventTitle;
    String eventContext;
}
