package com.example.spring_webflux_mongo.controller;

import com.example.spring_webflux_mongo.mapper.BookMapper;
import com.example.spring_webflux_mongo.model.Book;
import com.example.spring_webflux_mongo.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<BookResponse> getBook() {
        return bookService.getBooks().map(bookMapper::toBookResponse);
    }

    @GetMapping("/{id}")
    public Mono<BookResponse> getBook(@PathVariable String id) {
        return bookService.validateAndGetBookById(id).map(bookMapper::toBookResponse);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<BookResponse> createBook(@Valid @RequestBody CreateBookRequest createBookRequest) {
        Book book = bookMapper.toBook(createBookRequest);
        return bookService.saveBook(book).map(bookMapper::toBookResponse);
    }

    @PatchMapping("/{id}")
    public Mono<BookResponse> updateBook(@PathVariable String id,
                                         @RequestBody UpdateBookRequest updateBookRequest) {
        return bookService.validateAndGetBookById(id)
                .doOnSuccess(book -> {
                    bookMapper.updateBookFromRequest(updateBookRequest, book);
                    bookService.saveBook(book).subscribe();
                })
                .map(bookMapper::toBookResponse);
    }

    @DeleteMapping("/{id}")
    public Mono<BookResponse> deleteBook(@PathVariable String id) {
        return bookService.validateAndGetBookById(id)
                .doOnSuccess(book -> bookService.deleteBook(book).subscribe())
                .map(bookMapper::toBookResponse);
    }


}


