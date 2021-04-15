ALTER TABLE conference
    ADD column public_link TEXT;

CREATE table subscription
(
    id            SERIAL PRIMARY KEY,
    email         TEXT NOT NULL,
    conference_id INTEGER NOT NULL
);

ALTER TABLE subscription
    ADD CONSTRAINT conference_fk FOREIGN KEY (conference_id) REFERENCES conference (id);

