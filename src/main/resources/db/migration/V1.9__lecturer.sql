CREATE TABLE lecturer
(
    id        SERIAL PRIMARY KEY,
    email     TEXT UNIQUE NOT NULL,
    name TEXT        NOT NULL,
    surname  TEXT        NOT NULL,
    subscriptions BOOLEAN NOT NULL
);