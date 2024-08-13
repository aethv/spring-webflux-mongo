package com.example.spring_webflux_mongo;

import com.example.spring_webflux_mongo.controller.dto.BookResponse;
import com.example.spring_webflux_mongo.controller.dto.CreateBookRequest;
import com.example.spring_webflux_mongo.controller.dto.UpdateBookRequest;
import com.example.spring_webflux_mongo.model.Book;
import com.example.spring_webflux_mongo.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportTestcontainers(MyContainers.class)
class BookApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookRepository bookRepository;

    private static final String API_BOOKS_URL = "/api/books";
    private static final String API_BOOKS_ID_URL = "/api/books/%s";

    @BeforeEach
    void setup() {
        bookRepository.deleteAll().block();
    }

    private Book getDefaultBook() {
        return new Book("title", "author", 2023);
    }

    @Test
    void testGetBookWhenThereIsNone() {
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
        bookRepository.save(getDefaultBook())
                .doOnSuccess(book -> webTestClient.get()
                        .uri(API_BOOKS_URL)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_NDJSON_VALUE)
                        .expectBodyList(BookResponse.class)
                        .hasSize(1)
                        .consumeWith(response -> {
                            assertThat(response.getResponseBody()).isNotNull();
                            assertThat(response.getResponseBody().get(0).id()).isEqualTo(book.getId());
                            assertThat(response.getResponseBody().get(0).title()).isEqualTo(book.getTitle());
                            assertThat(response.getResponseBody().get(0).author()).isEqualTo(book.getAuthor());
                            assertThat(response.getResponseBody().get(0).year()).isEqualTo(book.getYear());
                        }));
    }

    @Test
    void testGetBookWhenNonExistent() {
        webTestClient.get()
                .uri(API_BOOKS_ID_URL.formatted("123"))
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookResponse.class);
    }

    @Test
    void testGetBookWhenExistent() {
        bookRepository.save(getDefaultBook())
                .doOnSuccess(book -> webTestClient.get()
                        .uri(API_BOOKS_ID_URL.formatted(book.getId()))
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(BookResponse.class)
                        .consumeWith(response -> {
                            assertThat(response.getResponseBody()).isNotNull();
                            assertThat(response.getResponseBody().id()).isEqualTo(book.getId());
                            assertThat(response.getResponseBody().title()).isEqualTo(book.getTitle());
                            assertThat(response.getResponseBody().author()).isEqualTo(book.getAuthor());
                            assertThat(response.getResponseBody().year()).isEqualTo(book.getYear());
                        }));
    }

    @Test
    void testCreateBook() {
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
                    assertThat(response.getResponseBody().id()).isNotNull();
                    assertThat(response.getResponseBody().title()).isEqualTo(createBookRequest.title());
                    assertThat(response.getResponseBody().author()).isEqualTo(createBookRequest.author());
                    assertThat(response.getResponseBody().year()).isEqualTo(createBookRequest.year());

                    String bookId = response.getResponseBody().id();
                    bookRepository.findById(bookId)
                            .doOnSuccess(book -> {
                                assertThat(book.getId()).isEqualTo(bookId);
                                assertThat(book.getTitle()).isEqualTo(createBookRequest.title());
                                assertThat(book.getAuthor()).isEqualTo(createBookRequest.author());
                                assertThat(book.getYear()).isEqualTo(createBookRequest.year());
                            });
                });
    }

    @Test
    void testUpdateBook() {
        bookRepository.save(getDefaultBook())
                .doOnSuccess(book -> {
                    UpdateBookRequest updateBookRequest = new UpdateBookRequest("newTitle", "newAuthor", 2024);

                    webTestClient.patch()
                            .uri(API_BOOKS_ID_URL.formatted(book.getId()))
                            .body(Mono.just(updateBookRequest), UpdateBookRequest.class)
                            .exchange()
                            .expectStatus().isOk()
                            .expectHeader().contentType(MediaType.APPLICATION_JSON)
                            .expectBody(BookResponse.class)
                            .consumeWith(response -> {
                                assertThat(response.getResponseBody()).isNotNull();
                                assertThat(response.getResponseBody().id()).isNotNull();
                                assertThat(response.getResponseBody().title()).isEqualTo(updateBookRequest.title());
                                assertThat(response.getResponseBody().author()).isEqualTo(updateBookRequest.author());
                                assertThat(response.getResponseBody().year()).isEqualTo(updateBookRequest.year());

                                String bookId = response.getResponseBody().id();
                                bookRepository.findById(bookId)
                                        .doOnSuccess(bookUpdated -> {
                                            assertThat(bookUpdated.getId()).isEqualTo(bookId);
                                            assertThat(bookUpdated.getTitle()).isEqualTo(updateBookRequest.title());
                                            assertThat(bookUpdated.getAuthor()).isEqualTo(updateBookRequest.author());
                                            assertThat(bookUpdated.getYear()).isEqualTo(updateBookRequest.year());
                                        });
                            });
                });
    }

    @Test
    void testDeleteBookWhenNonExistent() {
        webTestClient.delete()
                .uri(API_BOOKS_ID_URL.formatted("123"))
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookResponse.class);
    }

    @Test
    void testDeleteBookWhenExistent() {
        bookRepository.save(getDefaultBook())
                .doOnSuccess(book -> webTestClient.delete()
                        .uri(API_BOOKS_ID_URL.formatted(book.getId()))
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(BookResponse.class)
                        .consumeWith(response -> {
                            assertThat(response.getResponseBody()).isNotNull();
                            assertThat(response.getResponseBody().id()).isEqualTo(book.getId());
                            assertThat(response.getResponseBody().title()).isEqualTo(book.getTitle());
                            assertThat(response.getResponseBody().author()).isEqualTo(book.getAuthor());
                            assertThat(response.getResponseBody().year()).isEqualTo(book.getYear());
                        }));
    }
}
