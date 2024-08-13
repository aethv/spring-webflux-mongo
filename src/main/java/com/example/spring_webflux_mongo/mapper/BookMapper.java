package com.example.spring_webflux_mongo.mapper;

import com.example.spring_webflux_mongo.controller.dto.BookResponse;
import com.example.spring_webflux_mongo.controller.dto.CreateBookRequest;
import com.example.spring_webflux_mongo.controller.dto.UpdateBookRequest;
import com.example.spring_webflux_mongo.model.Book;


public interface BookMapper{

    Book toBook(CreateBookRequest request);

    void updateBookFromRequest(UpdateBookRequest request, Book book);

    BookResponse toBookResponse(Book book);
}
