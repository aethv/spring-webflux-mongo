package com.example.spring_webflux_mongo.controller.dto;

public record UpdateBookRequest (
        String title,
        String author,
        Integer year
) {
}
