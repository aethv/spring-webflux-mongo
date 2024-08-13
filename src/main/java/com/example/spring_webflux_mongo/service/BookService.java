package com.example.spring_webflux_mongo.service;

import com.example.spring_webflux_mongo.exception.NotFoundException;
import com.example.spring_webflux_mongo.model.Book;
import com.example.spring_webflux_mongo.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookService {

    Flux<Book> getBooks();

    Mono<Book> validateAndGetBookById(String id);

    Mono<Book> saveBook(Book book);

    Mono<Void> deleteBook(Book book);
}
