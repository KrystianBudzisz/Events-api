package com.example.eventsapi.event;

import com.example.eventsapi.exception.DatabaseException;
import com.example.eventsapi.mapper.EventMapper;
import com.example.eventsapi.model.CreateEventCommand;
import com.example.eventsapi.model.Event;
import com.example.eventsapi.model.EventDto;
import com.example.eventsapi.model.Place;
import com.example.eventsapi.repository.EventRepository;
import com.example.eventsapi.repository.PlaceRepository;
import com.example.eventsapi.service.EventService;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.ConcurrentModificationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private EventMapper eventMapper;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;


    @Test
    public void testCreateEventWithValidData() {
        CreateEventCommand cmd = new CreateEventCommand("EventName", LocalDateTime.now().plusDays(1), 100.0, 50, "PlaceCode");
        Place place = new Place();
        place.setCapacity(100);
        when(placeRepository.findByCode("PlaceCode")).thenReturn(place);
        Event createdEvent = new Event();
        EventDto eventDto = new EventDto();
        when(eventRepository.save(any(Event.class))).thenReturn(createdEvent);
        when(eventMapper.toDto(createdEvent)).thenReturn(eventDto);

        EventDto result = eventService.createEvent(cmd);

        verify(eventRepository).save(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();

        assertEquals("EventName", capturedEvent.getName());
        assertEquals(100.0, capturedEvent.getPrice());
        assertEquals(50, capturedEvent.getNumberOfPeople());
        assertEquals(place, capturedEvent.getPlace());

        assertEquals(eventDto, result);
    }


    @Test
    public void testCreateEventWithDatabaseException() {
        CreateEventCommand cmd = new CreateEventCommand("EventName", LocalDateTime.now().plusDays(1), 100.0, 50, "PlaceCode");
        Place place = new Place();
        place.setCapacity(100);
        when(placeRepository.findByCode("PlaceCode")).thenReturn(place);
        when(eventRepository.save(any(Event.class))).thenThrow(new DataIntegrityViolationException("DB Error"));

        assertThrows(DatabaseException.class, () -> eventService.createEvent(cmd));
    }

    @Test
    public void testGetEvents() {
        Page<Event> mockPage = mock(Page.class);
        when(eventRepository.findAll(any(Pageable.class))).thenReturn(mockPage);
        when(mockPage.map(any())).thenReturn(mock(Page.class));

        Pageable mockPageable = mock(Pageable.class);
        eventService.getEvents(mockPageable);

        verify(eventRepository).findAll(pageableCaptor.capture());
        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(mockPageable, capturedPageable);
    }


    @Test
    public void testCreateEventWithOptimisticLocking() {
        CreateEventCommand cmd = new CreateEventCommand("EventName", LocalDateTime.now().plusDays(1), 100.0, 50, "PlaceCode");
        Place place = new Place();
        place.setCapacity(100);
        when(placeRepository.findByCode("PlaceCode")).thenReturn(place);
        when(eventRepository.save(any(Event.class))).thenThrow(new OptimisticLockException());

        assertThrows(ConcurrentModificationException.class, () -> eventService.createEvent(cmd));
    }

    @Test
    public void testGetEventsWithEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> emptyPage = new PageImpl<>(Collections.emptyList());
        when(eventRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<EventDto> result = eventService.getEvents(pageable);

        assertNotNull(result, "The result should not be null.");
        assertTrue(result.isEmpty(), "The result should be empty.");
    }


    @Test
    public void testCreateEventWithNearFutureDate() {
        LocalDateTime nearFuture = LocalDateTime.now().plusDays(1);
        CreateEventCommand cmd = new CreateEventCommand("EventName", nearFuture, 100.0, 50, "PlaceCode");

        Place place = new Place();
        place.setCapacity(100);
        when(placeRepository.findByCode("PlaceCode")).thenReturn(place);

        Event createdEvent = new Event();
        EventDto eventDto = new EventDto();
        when(eventRepository.save(any(Event.class))).thenReturn(createdEvent);
        when(eventMapper.toDto(createdEvent)).thenReturn(eventDto);

        EventDto result = eventService.createEvent(cmd);
        assertEquals(eventDto, result);
    }

    @Test
    public void testCreateEvent_ExceedsPlaceCapacity() {
        CreateEventCommand createEventCommand = new CreateEventCommand();
        createEventCommand.setNumberOfPeople(100);
        createEventCommand.setPlaceCode("PLACE123");

        Place mockedPlace = new Place();
        mockedPlace.setCapacity(50);

        when(placeRepository.findByCode("PLACE123")).thenReturn(mockedPlace);

        assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(createEventCommand);
        }, "Number of people exceeds place capacity!");
    }


}
