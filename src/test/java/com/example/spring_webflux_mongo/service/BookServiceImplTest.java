package com.example.spring_webflux_mongo.service;

import com.example.spring_webflux_mongo.exception.NotFoundException;
import com.example.spring_webflux_mongo.model.Book;
import com.example.spring_webflux_mongo.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(BookServiceImpl.class)
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @MockBean
    private BookRepository bookRepository;

    @Test
    void testGetBooksWhenThereIsNoBook() {
        when(bookRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(bookService.getBooks())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testGetBooksWhenThereIsOneBook() {
        Book book = getDefaultBook();
        when(bookRepository.findAll()).thenReturn(Flux.just(book));

        StepVerifier.create(bookService.getBooks())
                .expectNext(book)
                .verifyComplete();
    }

    @Test
    void testValidateAndGetBookByIdWhenExisting() {
        Book book = getDefaultBook();
        when(bookRepository.findById(anyString())).thenReturn(Mono.just(book));

        StepVerifier.create(bookService.validateAndGetBookById("123"))
                .consumeNextWith(bookFound -> assertThat(bookFound).isEqualTo(book))
                .verifyComplete();
    }

    @Test
    void testValidateAndGetBookByIdWhenNonExisting() {
        when(bookRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(bookService.validateAndGetBookById("123"))
                .verifyErrorMatches(ex -> ex instanceof NotFoundException);
    }

    @Test
    void testSaveBook() {
        Book book = getDefaultBook();
        when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(book));

        StepVerifier.create(bookService.saveBook(book))
                .consumeNextWith(bookFound -> assertThat(bookFound).isEqualTo(book))
                .verifyComplete();
    }

    @Test
    void testDeleteBook() {
        Book book = getDefaultBook();
        when(bookRepository.delete(any(Book.class))).thenReturn(Mono.empty());

        StepVerifier.create(bookService.deleteBook(book))
                .verifyComplete();
    }

    private Book getDefaultBook() {
        return new Book("123", "title", "author", 2023);
    }
}
