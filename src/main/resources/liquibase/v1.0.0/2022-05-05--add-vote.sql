--liquibase formatted sql

--changeset mihaildemidoff:add-vote splitStatements:false runOnChange:false logicalFilePath:liquibase/add-vote/1.sql
--comment Добавляем таблицу vote

CREATE TABLE vote
(
    id             BIGSERIAL PRIMARY KEY,
    poll_id        BIGINT    NOT NULL,
    deck_option_id BIGINT    NOT NULL,
    user_id        BIGINT    NOT NULL,
    username       VARCHAR,
    first_name     VARCHAR,
    last_name      VARCHAR,
    created_date   TIMESTAMP NOT NULL,
    modified_date  TIMESTAMP NOT NULL,
    CONSTRAINT fk_vote_poll_id FOREIGN KEY (poll_id) REFERENCES poll (id),
    CONSTRAINT fk_vote_deck_option_id FOREIGN KEY (deck_option_id) REFERENCES deck_option (id)
);

CREATE UNIQUE INDEX ux_poll_id_user_id_vote ON vote (poll_id, user_id);
