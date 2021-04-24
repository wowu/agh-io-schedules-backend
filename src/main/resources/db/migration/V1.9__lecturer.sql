CREATE TABLE lecturer
(
    id        SERIAL PRIMARY KEY,
    email     TEXT UNIQUE NOT NULL,
    firstname TEXT        NOT NULL,
    lastname  TEXT        NOT NULL,
    subscriptions BOOLEAN NOT NULL
);