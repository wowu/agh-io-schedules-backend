CREATE TABLE person
(
    id        SERIAL PRIMARY KEY,
    email     TEXT UNIQUE NOT NULL,
    firstname TEXT        NOT NULL,
    lastname  TEXT        NOT NULL,
    role      TEXT        NOT NULL
);

CREATE TABLE account
(
    id        SERIAL PRIMARY KEY,
    person_id INTEGER UNIQUE NOT NULL,
    password  TEXT           NOT NULL
);

ALTER TABLE account
    ADD CONSTRAINT person_fk FOREIGN KEY (person_id) REFERENCES person (id);