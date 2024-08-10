package com.example.spring_webflux_mongo.controller;

public record UpdateBookRequest (
        String title,
        String author,
        Integer year
) {
}
