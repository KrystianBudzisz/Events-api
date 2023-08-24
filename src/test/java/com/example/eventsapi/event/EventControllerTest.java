package com.example.eventsapi.event;

import com.example.eventsapi.model.CreateEventCommand;
import com.example.eventsapi.model.Event;
import com.example.eventsapi.model.Place;
import com.example.eventsapi.repository.EventRepository;
import com.example.eventsapi.repository.PlaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hamcrest.Matchers;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Place place;

    @BeforeEach
    public void setUp() {
        eventRepository.deleteAll();
        placeRepository.deleteAll();

        place = new Place();
        place.setName("Sample Place");
        place.setCode("CODE001");
        place.setCapacity(100);
        place = placeRepository.save(place);
    }

    @Test
    public void shouldCreateEvent() throws Exception {
        CreateEventCommand command = new CreateEventCommand(
                "Test Event",
                LocalDateTime.now().plusDays(2),
                50.0,
                20,
                place.getCode());

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Test Event")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.placeCode", Matchers.is(place.getCode())));

        List<Event> events = eventRepository.findAll();
        assertEquals(1, events.size());
        assertEquals("Test Event", events.get(0).getName());

        Hibernate.initialize(events.get(0).getPlace());

        assertEquals(place.getCode(), events.get(0).getPlace().getCode());
    }

    @Test
    public void shouldGetAllEvents() throws Exception {
        Event event1 = new Event();
        event1.setName("Event 1");
        event1.setDate(LocalDateTime.now().plusDays(2));
        event1.setPrice(50.0);
        event1.setNumberOfPeople(20);
        event1.setPlace(place);
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setName("Event 2");
        event2.setDate(LocalDateTime.now().plusDays(3));
        event2.setPrice(60.0);
        event2.setNumberOfPeople(30);
        event2.setPlace(place);
        eventRepository.save(event2);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name", Matchers.is("Event 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name", Matchers.is("Event 2")));

        List<Event> events = eventRepository.findAll();
        assertEquals(2, events.size());
        assertTrue(events.stream().anyMatch(e -> "Event 1".equals(e.getName())));
        assertTrue(events.stream().anyMatch(e -> "Event 2".equals(e.getName())));
    }

    @Test
    public void shouldFailToCreateEventDueToPastDate() throws Exception {
        CreateEventCommand command = new CreateEventCommand(
                "Test Event",
                LocalDateTime.now().minusDays(2),
                50.0,
                20,
                place.getCode());

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldHaveCorrectFieldAnnotations() throws NoSuchFieldException {
        Field nameField = CreateEventCommand.class.getDeclaredField("name");
        assertTrue(nameField.isAnnotationPresent(NotBlank.class));
        assertEquals("Event name cannot be empty", nameField.getAnnotation(NotBlank.class).message());

        Field dateField = CreateEventCommand.class.getDeclaredField("date");
        assertTrue(dateField.isAnnotationPresent(NotNull.class));
        assertEquals("Event date cannot be null", dateField.getAnnotation(NotNull.class).message());
        assertTrue(dateField.isAnnotationPresent(Future.class));
        assertEquals("Event date cannot be in the past", dateField.getAnnotation(Future.class).message());
        assertEquals(LocalDateTime.class, dateField.getType());

        Field priceField = CreateEventCommand.class.getDeclaredField("price");
        assertTrue(priceField.isAnnotationPresent(Min.class));
        assertEquals("Price cannot be negative", priceField.getAnnotation(Min.class).message());

        Field numberOfPeopleField = CreateEventCommand.class.getDeclaredField("numberOfPeople");
        assertTrue(numberOfPeopleField.isAnnotationPresent(Min.class));
        assertEquals("Number of people must be at least 1", numberOfPeopleField.getAnnotation(Min.class).message());

        Field placeCodeField = CreateEventCommand.class.getDeclaredField("placeCode");
        assertTrue(placeCodeField.isAnnotationPresent(NotBlank.class));
        assertEquals("Place code cannot be empty", placeCodeField.getAnnotation(NotBlank.class).message());
    }

}






