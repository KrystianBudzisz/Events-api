package com.example.eventsapi.controller;

import com.example.eventsapi.model.CreateEventCommand;
import com.example.eventsapi.model.EventDto;
import com.example.eventsapi.service.EventService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody CreateEventCommand createEventCommand) { //@Valid
        EventDto eventDto = eventService.createEvent(createEventCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventDto);
    }

    @GetMapping
    public ResponseEntity<Page<EventDto>> getEvents(@PageableDefault(size = 10) Pageable pageable) {
        Page<EventDto> events = eventService.getEvents(pageable);
        return ResponseEntity.ok(events);
    }
}