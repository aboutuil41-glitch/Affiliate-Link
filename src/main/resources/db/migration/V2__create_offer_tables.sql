CREATE TABLE offers (
id            BIGINT AUTO_INCREMENT PRIMARY KEY,
advertiser_id BIGINT       NOT NULL,
title         VARCHAR(255) NOT NULL,
description   TEXT,
landing_url   VARCHAR(500) NOT NULL,
niche         VARCHAR(100) NOT NULL,
created_at    DATETIME(6)  NOT NULL
);

CREATE TABLE campaigns (
   id           BIGINT AUTO_INCREMENT PRIMARY KEY,
   offer_id     BIGINT        NOT NULL,
   name         VARCHAR(255)  NOT NULL,
   start_date   DATE          NOT NULL,
   end_date     DATE          NOT NULL,
   budget       DECIMAL(12,2) NOT NULL,
   payout_model VARCHAR(20)   NOT NULL,
   payout_value DECIMAL(12,2) NOT NULL,
   status       VARCHAR(20)   NOT NULL,
   CONSTRAINT fk_campaigns_offer FOREIGN KEY (offer_id) REFERENCES offers (id)

);

CREATE TABLE tags (
id   BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL,
CONSTRAINT uq_tags_name UNIQUE (name)
);

CREATE TABLE offer_tags (
    offer_id BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    PRIMARY KEY (offer_id, tag_id),
    CONSTRAINT fk_offer_tags_offer FOREIGN KEY (offer_id) REFERENCES offers (id),
    CONSTRAINT fk_offer_tags_tag   FOREIGN KEY (tag_id)   REFERENCES tags (id)
);