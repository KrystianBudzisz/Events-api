package com.example.eventsapi.service;

import com.example.eventsapi.exception.DatabaseException;
import com.example.eventsapi.mapper.EventMapper;
import com.example.eventsapi.model.CreateEventCommand;
import com.example.eventsapi.model.Event;
import com.example.eventsapi.model.EventDto;
import com.example.eventsapi.model.Place;
import com.example.eventsapi.repository.EventRepository;
import com.example.eventsapi.repository.PlaceRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ConcurrentModificationException;

@AllArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;

    private final PlaceRepository placeRepository;

    private final EventMapper eventMapper;

    @Transactional
    public EventDto createEvent(CreateEventCommand createEventCommand) {

        Place place = placeRepository.findByCode(createEventCommand.getPlaceCode());

        Event event = new Event();
        event.setName(createEventCommand.getName());
        event.setDate(createEventCommand.getDate());
        event.setPrice(createEventCommand.getPrice());
        event.setNumberOfPeople(createEventCommand.getNumberOfPeople());
        event.setPlace(place);

        try {
            event = eventRepository.save(event);
        } catch (DataIntegrityViolationException ex) {
            throw new DatabaseException("Error occurred while creating the event");
        } catch (OptimisticLockException ole) {
            throw new ConcurrentModificationException("The event was modified by another transaction. Please try again.");
        }

        return eventMapper.toDto(event);
    }

    public Page<EventDto> getEvents(Pageable pageable) {
        Page<Event> events = eventRepository.findAll(pageable);
        return events.map(eventMapper::toDto);
    }

}