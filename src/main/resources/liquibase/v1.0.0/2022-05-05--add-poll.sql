--liquibase formatted sql

--changeset mihaildemidoff:add-poll splitStatements:false runOnChange:false logicalFilePath:liquibase/add-poll/1.sql
--comment Добавляем таблицу poll

CREATE TABLE poll
(
    id                   BIGSERIAL PRIMARY KEY,
    deck_id              BIGINT    NOT NULL,
    status               VARCHAR   NOT NULL,
    message_id           VARCHAR   NOT NULL,
    author_id            BIGINT    NOT NULL,
    query                VARCHAR   NOT NULL,
    need_refresh         BOOLEAN   NOT NULL,
    processing_status    VARCHAR   NOT NULL,
    last_processing_date TIMESTAMP,
    created_date         TIMESTAMP NOT NULL,
    modified_date        TIMESTAMP NOT NULL,
    CONSTRAINT fk_poll_deck_id FOREIGN KEY (deck_id) REFERENCES deck (id)
);
