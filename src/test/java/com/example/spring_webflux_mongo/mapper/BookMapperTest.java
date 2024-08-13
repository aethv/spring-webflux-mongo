package com.example.spring_webflux_mongo.mapper;

import com.example.spring_webflux_mongo.controller.dto.BookResponse;
import com.example.spring_webflux_mongo.controller.dto.CreateBookRequest;
import com.example.spring_webflux_mongo.controller.dto.UpdateBookRequest;
import com.example.spring_webflux_mongo.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@Import(BookMapperImpl.class)
class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;

    @Test
    void testToBook() {
        CreateBookRequest createBookRequest = new CreateBookRequest("title", "author", 2023);

        Book book = bookMapper.toBook(createBookRequest);

        assertThat(book.getId()).isNull();
        assertThat(book.getTitle()).isEqualTo("title");
        assertThat(book.getAuthor()).isEqualTo("author");
        assertThat(book.getYear()).isEqualTo(2023);
    }

    @ParameterizedTest
    @MethodSource("provideUpdateBookRequests")
    void testUpdateBookFromUpdateBookRequest(UpdateBookRequest updateupdateBookRequest, Book expectedBook) {
        Book book = new Book("title", "author",2023);

        bookMapper.updateBookFromRequest(updateupdateBookRequest, book);

        assertThat(book.getId()).isEqualTo(expectedBook.getId());
        assertThat(book.getTitle()).isEqualTo(expectedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(expectedBook.getAuthor());
        assertThat(book.getYear()).isEqualTo(expectedBook.getYear());
    }

    private static Stream<Arguments> provideUpdateBookRequests() {
        return Stream.of(
                Arguments.of(new UpdateBookRequest("newTitle", null, null), new Book("newTitle", "author", 2023)),
                Arguments.of(new UpdateBookRequest(null, "newAuthor", null), new Book("title", "newAuthor", 2023)),
                Arguments.of(new UpdateBookRequest(null, null, 2024), new Book("title", "author", 2024)),
                Arguments.of(new UpdateBookRequest("newTitle", "newAuthor", 2024), new Book("newTitle", "newAuthor", 2024))
        );
    }

    @Test
    void testToBookResponse() {
        Book book = new Book("title", "author", 2023);

        BookResponse bookResponse = bookMapper.toBookResponse(book);

        assertThat(bookResponse.id()).isNull();
        assertThat(bookResponse.title()).isEqualTo("title");
        assertThat(bookResponse.author()).isEqualTo("author");
        assertThat(bookResponse.year()).isEqualTo(2023);
    }
}
