package com.affiliateLink;
public abstract class AbstractMySqlIT {

    @ServiceConnection
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4");

    static {
        MYSQL.start();
    }
}