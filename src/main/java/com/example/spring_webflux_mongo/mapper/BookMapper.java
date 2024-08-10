package com.example.spring_webflux_mongo.mapper;

import com.example.spring_webflux_mongo.controller.BookResponse;
import com.example.spring_webflux_mongo.controller.CreateBookRequest;
import com.example.spring_webflux_mongo.controller.UpdateBookRequest;
import com.example.spring_webflux_mongo.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toBook(CreateBookRequest request) {
        if (request == null) {
            return null;
        }
        return new Book(request.title(), request.author(), request.year());
    }

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

    public BookResponse toBookResponse(Book book) {
        if (book == null) {
            return null;
        }
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getYear());
    }
}
