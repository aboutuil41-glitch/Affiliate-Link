CREATE TABLE profiles (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  email      VARCHAR(255) NOT NULL UNIQUE,
  status     VARCHAR(255) NOT NULL,
  created_at DATE         NOT NULL
);

CREATE TABLE publishers (
    id         BIGINT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    niche      VARCHAR(255) NOT NULL,
    bio        VARCHAR(255),
    country    VARCHAR(255) NOT NULL,
    FOREIGN KEY (id) REFERENCES profiles(id)
);

CREATE TABLE advertisers (
     id           BIGINT PRIMARY KEY,
     company_name VARCHAR(255) NOT NULL,
     sector       VARCHAR(255) NOT NULL,
     website      VARCHAR(255) NOT NULL,
     FOREIGN KEY (id) REFERENCES profiles(id)
);