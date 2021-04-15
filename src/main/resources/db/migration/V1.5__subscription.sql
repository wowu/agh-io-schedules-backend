-- Usuniecie person (nie bedzie potrzebna) i zmodyfikowanie my_user
DROP TABLE my_user;
DROP TABLE person;

CREATE TABLE my_user
(
    id        SERIAL PRIMARY KEY,
    email     TEXT UNIQUE NOT NULL,
    firstname TEXT        NOT NULL,
    lastname  TEXT        NOT NULL,
    password  TEXT        NOT NULL,
    role      TEXT        NOT NULL
);

INSERT INTO my_user(email, firstname, lastname, password, role)
VALUES ('admin@io.pl', 'Admin', 'Admin',
        '$argon2id$v=19$m=4096,t=3,p=1$p7AByGF2CfgDBmuTQzA7Fg$2OzCRRXBwNk6szcm+hZzDjNwq8EzkY9b+rXAvVcNqig', 'ADMIN');

-- Dodanie public_link do harmonogramu
ALTER TABLE schedule
    ADD column public_link TEXT;

-- Dodanie tabelki subskrypcji
CREATE table subscription
(
    id          SERIAL PRIMARY KEY,
    email       TEXT    NOT NULL,
    schedule_id INTEGER NOT NULL
);

ALTER TABLE subscription
    ADD CONSTRAINT schedule_fk FOREIGN KEY (schedule_id) REFERENCES schedule (id);

