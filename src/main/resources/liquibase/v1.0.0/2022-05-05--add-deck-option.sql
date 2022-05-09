--liquibase formatted sql

--changeset mihaildemidoff:add-deck-option splitStatements:false runOnChange:false logicalFilePath:liquibase/add-deck-option/1.sql
--comment Добавляем таблицу deck_option

CREATE TABLE deck_option
(
    id            BIGSERIAL PRIMARY KEY,
    deck_id       BIGINT    NOT NULL,
    text          VARCHAR   NOT NULL,
    row           INT       NOT NULL,
    index         INT       NOT NULL,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_deck_option_deck_id FOREIGN KEY (deck_id) REFERENCES deck (id)
);

CREATE UNIQUE INDEX ux_deck_id_row_index_deck_option ON deck_option (deck_id, row, index);
CREATE UNIQUE INDEX ux_deck_id_text_deck_option ON deck_option (deck_id, text);

INSERT INTO deck_option(deck_id, text, row, index, created_date, modified_date)
VALUES (((select id from deck where type = 'FIBONACCI')), '0', 0, 0, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '1', 0, 1, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '2', 0, 2, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '3', 0, 3, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '5', 0, 4, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '8', 0, 5, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '13', 0, 6, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '21', 0, 7, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '34', 0, 8, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '55', 0, 9, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '☕️', 1, 0, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '❓', 1, 1, now(), now()),
       ((select id from deck where type = 'FIBONACCI'), '∞', 1, 2, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '0', 0, 0, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '1', 0, 1, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '2', 0, 2, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '3', 0, 3, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '4', 0, 4, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '5', 0, 5, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '6', 0, 6, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '7', 0, 7, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '8', 0, 8, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '9', 0, 9, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '☕️', 1, 0, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '❓', 1, 1, now(), now()),
       ((select id from deck where type = 'SEQUENTIAL'), '∞', 1, 2, now(), now()),
       ((select id from deck where type = 'PLAYING_CARDS'), 'Ace', 0, 0, now(), now()),
       ((select id from deck where type = 'PLAYING_CARDS'), '2', 0, 1, now(), now()),
       ((select id from deck where type = 'PLAYING_CARDS'), '3', 0, 2, now(), now()),
       ((select id from deck where type = 'PLAYING_CARDS'), '5', 0, 3, now(), now()),
       ((select id from deck where type = 'PLAYING_CARDS'), '8', 0, 4, now(), now()),
       ((select id from deck where type = 'PLAYING_CARDS'), 'King', 0, 5, now(), now()),
       ((select id from deck where type = 'PLAYING_CARDS'), '☕️', 1, 0, now(), now()),
       ((select id from deck where type = 'PLAYING_CARDS'), '❓', 1, 1, now(), now()),
       ((select id from deck where type = 'PLAYING_CARDS'), '∞', 1, 2, now(), now()),
       ((select id from deck where type = 'SCRUM'), '0', 0, 0, now(), now()),
       ((select id from deck where type = 'SCRUM'), '1/2', 0, 1, now(), now()),
       ((select id from deck where type = 'SCRUM'), '1', 0, 2, now(), now()),
       ((select id from deck where type = 'SCRUM'), '2', 0, 3, now(), now()),
       ((select id from deck where type = 'SCRUM'), '3', 0, 4, now(), now()),
       ((select id from deck where type = 'SCRUM'), '5', 0, 5, now(), now()),
       ((select id from deck where type = 'SCRUM'), '8', 0, 6, now(), now()),
       ((select id from deck where type = 'SCRUM'), '13', 0, 7, now(), now()),
       ((select id from deck where type = 'SCRUM'), '20', 0, 8, now(), now()),
       ((select id from deck where type = 'SCRUM'), '40', 0, 9, now(), now()),
       ((select id from deck where type = 'SCRUM'), '100', 0, 10, now(), now()),
       ((select id from deck where type = 'SCRUM'), '☕️', 1, 0, now(), now()),
       ((select id from deck where type = 'SCRUM'), '❓', 1, 1, now(), now()),
       ((select id from deck where type = 'SCRUM'), '∞', 1, 2, now(), now()),
       ((select id from deck where type = 'T_SHIRT'), 'XS', 0, 0, now(), now()),
       ((select id from deck where type = 'T_SHIRT'), 'S', 0, 1, now(), now()),
       ((select id from deck where type = 'T_SHIRT'), 'M', 0, 2, now(), now()),
       ((select id from deck where type = 'T_SHIRT'), 'L', 0, 3, now(), now()),
       ((select id from deck where type = 'T_SHIRT'), 'XL', 0, 4, now(), now()),
       ((select id from deck where type = 'T_SHIRT'), 'XXL', 0, 5, now(), now()),
       ((select id from deck where type = 'T_SHIRT'), '☕️', 1, 0, now(), now()),
       ((select id from deck where type = 'T_SHIRT'), '❓', 1, 1, now(), now()),
       ((select id from deck where type = 'T_SHIRT'), '∞', 1, 2, now(), now());
