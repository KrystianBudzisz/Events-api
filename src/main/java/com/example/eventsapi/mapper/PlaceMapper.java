package com.example.eventsapi.mapper;

import com.example.eventsapi.model.Place;
import com.example.eventsapi.model.PlaceDto;
import org.springframework.stereotype.Component;

@Component
public class PlaceMapper {

    public PlaceDto toDto(Place place) {
        PlaceDto placeDto = new PlaceDto();
        placeDto.setName(place.getName());
        placeDto.setCode(place.getCode());
        placeDto.setCapacity(place.getCapacity());
        return placeDto;
    }

}
