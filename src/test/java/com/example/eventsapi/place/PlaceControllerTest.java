package com.example.eventsapi.place;

import com.example.eventsapi.model.CreatePlaceCommand;
import com.example.eventsapi.model.Place;
import com.example.eventsapi.repository.PlaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaceRepository placeRepository;

    private static Place place;

    @BeforeEach
    public void setUp() {
        place = new Place();
        place.setName("Test Place");
        place.setCode("TEST001");
        place.setCapacity(100);

        placeRepository.save(place);
    }

    @AfterEach
    public void tearDown() {
        placeRepository.deleteAll();
    }

    @Test
    public void testAddPlace() throws Exception {
        CreatePlaceCommand createPlaceCommand = new CreatePlaceCommand();
        createPlaceCommand.setName("New Test Place");
        createPlaceCommand.setCode("NEW001");
        createPlaceCommand.setCapacity(200);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPlaceCommand)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(createPlaceCommand.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(createPlaceCommand.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(createPlaceCommand.getCapacity()));

        Place savedPlace = placeRepository.findByCode(createPlaceCommand.getCode());
        assertNotNull(savedPlace);
        assertEquals(createPlaceCommand.getName(), savedPlace.getName());
        assertEquals(createPlaceCommand.getCapacity(), savedPlace.getCapacity());
    }

    @Test
    public void testGetPlaces() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/places")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value(place.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].code").value(place.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].capacity").value(place.getCapacity()));

        Place placeFromDb = placeRepository.findByCode(place.getCode());
        assertNotNull(placeFromDb);
        assertEquals(place.getName(), placeFromDb.getName());
        assertEquals(place.getCode(), placeFromDb.getCode());
        assertEquals(place.getCapacity(), placeFromDb.getCapacity());
    }

    @Test
    public void testAddPlace_InvalidName() throws Exception {
        CreatePlaceCommand createPlaceCommand = new CreatePlaceCommand();
        createPlaceCommand.setName("");
        createPlaceCommand.setCode("NEW001");
        createPlaceCommand.setCapacity(200);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPlaceCommand)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddPlace_InvalidCode() throws Exception {
        CreatePlaceCommand createPlaceCommand = new CreatePlaceCommand();
        createPlaceCommand.setName("New Test Place");
        createPlaceCommand.setCode("");
        createPlaceCommand.setCapacity(200);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPlaceCommand)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddPlace_InvalidCapacity() throws Exception {
        CreatePlaceCommand createPlaceCommand = new CreatePlaceCommand();
        createPlaceCommand.setName("New Test Place");
        createPlaceCommand.setCode("NEW001");
        createPlaceCommand.setCapacity(0);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPlaceCommand)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
