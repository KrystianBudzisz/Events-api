package com.example.eventsapi.repository;

import com.example.eventsapi.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Place findByCode(String code);

}