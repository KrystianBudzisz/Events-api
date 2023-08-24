package com.example.eventsapi.place;

import com.example.eventsapi.exception.DatabaseException;
import com.example.eventsapi.mapper.PlaceMapper;
import com.example.eventsapi.model.CreatePlaceCommand;
import com.example.eventsapi.model.Place;
import com.example.eventsapi.model.PlaceDto;
import com.example.eventsapi.repository.PlaceRepository;
import com.example.eventsapi.service.PlaceService;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaceServiceTest {

    @InjectMocks
    private PlaceService placeService;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PlaceMapper placeMapper;

    @Captor
    private ArgumentCaptor<Place> placeCaptor;

    @Test
    void testAddPlaceSuccessfully() {
        CreatePlaceCommand command = new CreatePlaceCommand("Test Place", "TEST001", 100);
        Place mockPlace = new Place();
        mockPlace.setName(command.getName());
        mockPlace.setCode(command.getCode());
        mockPlace.setCapacity(command.getCapacity());

        PlaceDto expectedDto = new PlaceDto();
        expectedDto.setName(mockPlace.getName());
        expectedDto.setCode(mockPlace.getCode());
        expectedDto.setCapacity(mockPlace.getCapacity());

        when(placeRepository.save(any(Place.class))).thenReturn(mockPlace);
        when(placeMapper.toDto(mockPlace)).thenReturn(expectedDto);

        PlaceDto result = placeService.addPlace(command);

        verify(placeRepository).save(placeCaptor.capture());
        Place savedPlace = placeCaptor.getValue();
        assertEquals(command.getName(), savedPlace.getName());
        assertEquals(command.getCode(), savedPlace.getCode());
        assertEquals(command.getCapacity(), savedPlace.getCapacity());

        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getCode(), result.getCode());
        assertEquals(expectedDto.getCapacity(), result.getCapacity());
    }

    @Test
    void testAddPlaceFailsOnDataIntegrityViolation() {
        CreatePlaceCommand command = new CreatePlaceCommand("Test Place", "TEST001", 100);
        when(placeRepository.save(any(Place.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DatabaseException.class, () -> placeService.addPlace(command));
    }

    @Test
    void testGetPlaces() {
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Place mockPlace1 = new Place();
        mockPlace1.setName("Test Place1");
        mockPlace1.setCode("TEST001");
        mockPlace1.setCapacity(100);

        Place mockPlace2 = new Place();
        mockPlace2.setName("Test Place2");
        mockPlace2.setCode("TEST002");
        mockPlace2.setCapacity(200);

        List<Place> places = Arrays.asList(mockPlace1, mockPlace2);
        Page<Place> pageResult = new PageImpl<>(places);

        when(placeRepository.findAll(pageable)).thenReturn(pageResult);
        when(placeMapper.toDto(mockPlace1)).thenReturn(new PlaceDto(mockPlace1.getName(), mockPlace1.getCode(), mockPlace1.getCapacity()));
        when(placeMapper.toDto(mockPlace2)).thenReturn(new PlaceDto(mockPlace2.getName(), mockPlace2.getCode(), mockPlace2.getCapacity()));

        Page<PlaceDto> results = placeService.getPlaces(pageable);

        assertEquals(2, results.getSize());
        assertEquals(mockPlace1.getName(), results.getContent().get(0).getName());
        assertEquals(mockPlace1.getCode(), results.getContent().get(0).getCode());
        assertEquals(mockPlace2.getName(), results.getContent().get(1).getName());
    }

    @Test
    void testAddPlaceThrowsDatabaseExceptionDueToDataIntegrityViolation() {
        CreatePlaceCommand command = new CreatePlaceCommand("Test Place", "TEST001", 100);

        when(placeRepository.save(any(Place.class)))
                .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

        assertThrows(DatabaseException.class, () -> placeService.addPlace(command), "Error occurred while creating the place");
    }
}

