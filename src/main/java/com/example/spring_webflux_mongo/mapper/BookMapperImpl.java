package com.example.spring_webflux_mongo.mapper;

import com.example.spring_webflux_mongo.controller.dto.BookResponse;
import com.example.spring_webflux_mongo.controller.dto.CreateBookRequest;
import com.example.spring_webflux_mongo.controller.dto.UpdateBookRequest;
import com.example.spring_webflux_mongo.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapperImpl implements BookMapper{

    @Override
    public Book toBook(CreateBookRequest request) {
        if (request == null) {
            return null;
        }
        return new Book(request.title(), request.author(), request.year());
    }

    @Override
    public void updateBookFromRequest(UpdateBookRequest request, Book book) {
        if (book == null || request == null) {
            return;
        }
        if (request.title() != null)
            book.setTitle(request.title());

        if (request.author() != null)
            book.setAuthor(request.author());

        if (request.year() != null)
            book.setYear(request.year());
    }

    @Override
    public BookResponse toBookResponse(Book book) {
        if (book == null) {
            return null;
        }
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getYear());
    }
}
