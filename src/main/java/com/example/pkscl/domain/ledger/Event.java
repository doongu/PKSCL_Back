package com.example.pkscl.domain.ledger;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Event {
    @Id
    int eventnumber;
    int majornumber;
    String quarternumber;
    String eventtitle;
    String eventcontext;
}
