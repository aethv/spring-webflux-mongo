package com.example.spring_webflux_mongo;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

public interface MyContainers {

    @Container
    @ServiceConnection
    MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.4");
}
