package com.example.spring_webflux_mongo.controller;

import java.io.Serializable;

public record BookResponse(
        String id,
        String title,
        String author,
        Integer year
) implements Serializable {
}
