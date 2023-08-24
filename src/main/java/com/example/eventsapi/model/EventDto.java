package com.example.eventsapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventDto {

    private String name;
    private LocalDateTime date;
    private double price;
    private int numberOfPeople;
    private String placeCode;


}