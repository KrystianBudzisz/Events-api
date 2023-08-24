package com.example.eventsapi.mapper;

import com.example.eventsapi.model.Event;
import com.example.eventsapi.model.EventDto;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventDto toDto(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setName(event.getName());
        eventDto.setDate(event.getDate());
        eventDto.setPrice(event.getPrice());
        eventDto.setNumberOfPeople(event.getNumberOfPeople());
        eventDto.setPlaceCode(event.getPlace().getCode());
        return eventDto;
    }


}