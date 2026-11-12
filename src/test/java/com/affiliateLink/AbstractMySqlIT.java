package com.affiliateLink;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.mysql.MySQLContainer;

/**
 * Base class for every test that needs a real database.
 * Starts ONE MySQL container in Docker (shared by all tests), and Spring connects to it.
 */
@TestPropertySource(properties = "spring.docker.compose.enabled=false") // don't start your docker-compose in tests
public abstract class AbstractMySqlIT {

    @ServiceConnection
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4");

    static {
        MYSQL.start();
    }
}