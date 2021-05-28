CREATE TABLE notification
(
    id      SERIAL PRIMARY KEY,
    unit    TEXT    NOT NULL,
    value   INTEGER NOT NULL,
    user_id INTEGER NOT NULL
);

ALTER TABLE notification
    ADD CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES my_user (id);