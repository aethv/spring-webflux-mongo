package com.example.spring_webflux_mongo.service;

import com.example.spring_webflux_mongo.exception.NotFoundException;
import com.example.spring_webflux_mongo.model.Book;
import com.example.spring_webflux_mongo.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository repository;

    public Flux<Book> getBooks() {
        return repository.findAll();
    }

    public Mono<Book> validateAndGetBookById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(id)));
    }

    public Mono<Book> saveBook(Book book) {
        return repository.save(book);
    }

    public Mono<Void> deleteBook(Book book) {
        return repository.delete(book);
    }
}
