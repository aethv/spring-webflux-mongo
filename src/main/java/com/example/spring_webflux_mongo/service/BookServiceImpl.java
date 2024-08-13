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
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    @Override
    public Flux<Book> getBooks() {
        return repository.findAll();
    }

    @Override
    public Mono<Book> validateAndGetBookById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(id)));
    }

    @Override
    public Mono<Book> saveBook(Book book) {
        return repository.save(book);
    }

    @Override
    public Mono<Void> deleteBook(Book book) {
        return repository.delete(book);
    }
}
