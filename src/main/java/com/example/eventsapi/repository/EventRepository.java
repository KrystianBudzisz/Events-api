package com.example.eventsapi.repository;

import com.example.eventsapi.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAll(Pageable pageable);
    @Query("SELECT e FROM Event e JOIN FETCH e.place WHERE e.id = :id")
    Event findWithPlaceById(Long id);
}