CREATE TABLE person
(
    id        SERIAL PRIMARY KEY,
    email     TEXT UNIQUE NOT NULL,
    firstname TEXT        NOT NULL,
    lastname  TEXT        NOT NULL
);

CREATE TABLE my_user
(
    id        SERIAL PRIMARY KEY,
    person_id INTEGER UNIQUE NOT NULL,
    password  TEXT           NOT NULL,
    role      TEXT           NOT NULL
);

ALTER TABLE my_user
    ADD CONSTRAINT person_fk FOREIGN KEY (person_id) REFERENCES person (id);