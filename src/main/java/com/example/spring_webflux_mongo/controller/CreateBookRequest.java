package com.example.spring_webflux_mongo.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateBookRequest (
        @NotBlank String title,
        @NotBlank String author,
        @Positive Integer year){
}
