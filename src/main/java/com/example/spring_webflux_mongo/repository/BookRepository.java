package com.example.spring_webflux_mongo.repository;

import com.example.spring_webflux_mongo.model.Book;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BookRepository extends ReactiveMongoRepository<Book, String> {
}
