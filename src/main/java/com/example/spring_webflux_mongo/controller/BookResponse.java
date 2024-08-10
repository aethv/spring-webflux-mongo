package com.example.spring_webflux_mongo.controller;

public record BookResponse (
    String id,
    String title,
    String author,
    Integer year
) {
}
