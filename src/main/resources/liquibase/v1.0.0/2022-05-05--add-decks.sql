--liquibase formatted sql

--changeset mihaildemidoff:add-decks splitStatements:false runOnChange:false logicalFilePath:liquibase/add-decks/1.sql
--comment Добавляем таблицу deck

CREATE TABLE deck
(
    id            BIGSERIAL PRIMARY KEY,
    type          VARCHAR   NOT NULL,
    title         VARCHAR   NOT NULL,
    description   VARCHAR   NOT NULL,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX ux_type_deck ON deck (type);

INSERT INTO deck(type, title, description, created_date, modified_date)
VALUES ('FIBONACCI', 'Fibonacci', '0, 1, 2, 3, 5, 8...', now(), now()),
       ('SEQUENTIAL', 'Sequential', '0, 1, 2, 3, 4, 5...', now(), now()),
       ('PLAYING_CARDS', 'Playing cards', 'Ace, King, 2, 3, 5...', now(), now()),
       ('SCRUM', 'Scrum', '0, 1/2, 1, 2, 3, 5...', now(), now()),
       ('T_SHIRT', 'T-Shirt', 'XS, S, M, L, XL, XXL', now(), now());
