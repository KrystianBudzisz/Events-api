package com.example.eventsapi.service;

import com.example.eventsapi.exception.DatabaseException;
import com.example.eventsapi.mapper.PlaceMapper;
import com.example.eventsapi.model.CreatePlaceCommand;
import com.example.eventsapi.model.Place;
import com.example.eventsapi.model.PlaceDto;
import com.example.eventsapi.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final PlaceMapper placeMapper;

    @Transactional
    public PlaceDto addPlace(CreatePlaceCommand createPlaceCommand) {

        Place place = new Place();
        place.setCode(createPlaceCommand.getCode());
        place.setName(createPlaceCommand.getName());
        place.setCapacity(createPlaceCommand.getCapacity());

        try {
            place = placeRepository.save(place);
        } catch (DataIntegrityViolationException ex) {
            throw new DatabaseException("Error occurred while creating the place");
        }

        return placeMapper.toDto(place);
    }


    public Page<PlaceDto> getPlaces(Pageable pageable) {
        Page<Place> places = placeRepository.findAll(pageable);
        return places.map(placeMapper::toDto);
    }


}