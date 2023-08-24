package com.example.eventsapi.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateEventCommand {

    @NotBlank(message = "Event name cannot be empty")
    private String name;

    @NotNull(message = "Event date cannot be null")
    @Future(message = "Event date cannot be in the past")
    private LocalDateTime date;

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    @Min(value = 1, message = "Number of people must be at least 1")
    private int numberOfPeople;

    @NotBlank(message = "Place code cannot be empty")
    private String placeCode;
}