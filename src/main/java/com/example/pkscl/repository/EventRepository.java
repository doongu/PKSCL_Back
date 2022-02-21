package com.example.pkscl.repository;

import java.util.List;

import com.example.pkscl.domain.ledger.Event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByEventnumber(int eventnumber);
    void deleteByEventnumber(int eventnumber);
    List<Event> findByQuarteridOrderByEventsequence(int quarterid);
}
