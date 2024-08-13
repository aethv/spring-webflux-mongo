package com.example.spring_webflux_mongo.controller;

import com.example.spring_webflux_mongo.controller.dto.BookResponse;
import com.example.spring_webflux_mongo.controller.dto.CreateBookRequest;
import com.example.spring_webflux_mongo.controller.dto.UpdateBookRequest;
import com.example.spring_webflux_mongo.exception.NotFoundException;
import com.example.spring_webflux_mongo.mapper.BookMapperImpl;
import com.example.spring_webflux_mongo.model.Book;
import com.example.spring_webflux_mongo.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest
@Import(BookMapperImpl.class)
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookService bookService;

    private static final String API_BOOKS_URL = "/api/books";
    private static final String API_BOOKS_ID_URL = "/api/books/%s";

    @Test
    void testGetBooksWhenThereIsNone() {
        when(bookService.getBooks()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri(API_BOOKS_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_NDJSON_VALUE)
                .expectBodyList(BookResponse.class)
                .hasSize(0);
    }

    @Test
    void testGetBooksWhenThereIsOne() {
        Book book = getDefaultBook();
        when(bookService.getBooks()).thenReturn(Flux.just(book));

        webTestClient.get()
                .uri(API_BOOKS_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_NDJSON_VALUE)
                .expectBodyList(BookResponse.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isNotNull();
                    assertThat(response.getResponseBody().get(0).id()).isEqualTo(book.getId());
                    assertThat(response.getResponseBody().get(0).title()).isEqualTo(book.getTitle());
                    assertThat(response.getResponseBody().get(0).author()).isEqualTo(book.getAuthor());
                    assertThat(response.getResponseBody().get(0).year()).isEqualTo(book.getYear());
                });
    }

    @Test
    void testGetBookByImdbIdWhenNonExistent() {
        when(bookService.validateAndGetBookById(anyString())).thenReturn(Mono.error(new NotFoundException("123")));

        webTestClient.get()
                .uri(API_BOOKS_ID_URL.formatted("123"))
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookResponse.class);
    }

    @Test
    void testGetBookByImdbIdWhenExistent() {
        Book book = getDefaultBook();
        when(bookService.validateAndGetBookById(anyString())).thenReturn(Mono.just(book));

        webTestClient.get()
                .uri(API_BOOKS_ID_URL.formatted("123"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookResponse.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isNotNull();
                    assertThat(response.getResponseBody().id()).isEqualTo(book.getId());
                    assertThat(response.getResponseBody().title()).isEqualTo(book.getTitle());
                    assertThat(response.getResponseBody().author()).isEqualTo(book.getAuthor());
                    assertThat(response.getResponseBody().year()).isEqualTo(book.getYear());
                });
    }

    @Test
    void testCreateBook() {
        Book book = getDefaultBook();
        when(bookService.saveBook(any(Book.class))).thenReturn(Mono.just(book));

        CreateBookRequest createBookRequest = new CreateBookRequest("title", "author", 2023);

        webTestClient.post()
                .uri(API_BOOKS_URL)
                .body(Mono.just(createBookRequest), CreateBookRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookResponse.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isNotNull();
                    assertThat(response.getResponseBody().id()).isEqualTo(book.getId());
                    assertThat(response.getResponseBody().title()).isEqualTo(book.getTitle());
                    assertThat(response.getResponseBody().author()).isEqualTo(book.getAuthor());
                    assertThat(response.getResponseBody().year()).isEqualTo(book.getYear());
                });
    }

    @Test
    void testUpdateBook() {
        Book book = getDefaultBook();
        UpdateBookRequest updateBookRequest = new UpdateBookRequest("newTitle", "newActors", 2024);

        when(bookService.validateAndGetBookById(anyString())).thenReturn(Mono.just(book));
        when(bookService.saveBook(any(Book.class))).thenReturn(Mono.just(book));

        webTestClient.patch()
                .uri(API_BOOKS_ID_URL.formatted("123"))
                .body(Mono.just(updateBookRequest), UpdateBookRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookResponse.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isNotNull();
                    assertThat(response.getResponseBody().id()).isEqualTo(book.getId());
                    assertThat(response.getResponseBody().title()).isEqualTo(updateBookRequest.title());
                    assertThat(response.getResponseBody().author()).isEqualTo(updateBookRequest.author());
                    assertThat(response.getResponseBody().year()).isEqualTo(updateBookRequest.year());
                });
    }

    @Test
    void testDeleteBookWhenExistent() {
        Book book = getDefaultBook();

        when(bookService.validateAndGetBookById(anyString())).thenReturn(Mono.just(book));
        when(bookService.deleteBook(any(Book.class))).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(API_BOOKS_ID_URL.formatted("123"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookResponse.class);
    }

    @Test
    void testDeleteBookWhenNonExistent() {
        when(bookService.validateAndGetBookById(anyString())).thenReturn(Mono.error(new NotFoundException("123")));

        webTestClient.delete()
                .uri(API_BOOKS_ID_URL.formatted("123"))
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookResponse.class);
    }

    private Book getDefaultBook() {
        return new Book("123", "title", "author", 2023);
    }


}
