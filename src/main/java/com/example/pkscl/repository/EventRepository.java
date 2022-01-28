package com.example.pkscl.repository;

import com.example.pkscl.domain.ledger.Event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
