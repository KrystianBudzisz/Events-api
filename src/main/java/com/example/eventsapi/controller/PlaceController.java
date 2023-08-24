package com.example.eventsapi.controller;

import com.example.eventsapi.model.CreatePlaceCommand;
import com.example.eventsapi.model.PlaceDto;
import com.example.eventsapi.service.PlaceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/places")
public class PlaceController {


    private PlaceService placeService;

    @PostMapping
    public ResponseEntity<PlaceDto> addPlace(@Valid @RequestBody CreatePlaceCommand createPlaceCommand) { // @Valid
        PlaceDto placeDto = placeService.addPlace(createPlaceCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(placeDto);
    }

    @GetMapping
    public ResponseEntity<Page<PlaceDto>> getPlaces(@PageableDefault(size = 10) Pageable pageable) {
        Page<PlaceDto> places = placeService.getPlaces(pageable);
        return ResponseEntity.ok(places);
    }
}