package com.example.eventsapi.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreatePlaceCommand {
    @NotBlank(message = "Place name cannot be empty")
    private String name;

    @NotBlank(message = "Place code cannot be empty")
    private String code;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

}