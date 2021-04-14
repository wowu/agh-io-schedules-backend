CREATE TABLE meeting
(
    id              SERIAL PRIMARY KEY,
    conference_id   INTEGER   NOT NULL,
    date_start      TIMESTAMP NOT NULL,
    date_end        TIMESTAMP NOT NULL,
    subject         TEXT      NOT NULL,
    group_name      TEXT      NOT NULL,
    lecturer        TEXT      NOT NULL,
    type            TEXT      NOT NULL,
    length_in_hours INTEGER   NOT NULL,
    format          TEXT      NOT NULL,
    room            TEXT      NOT NULL
);

CREATE TABLE conference
(
    id          SERIAL PRIMARY KEY,
    schedule_id INTEGER NOT NULL
);

CREATE TABLE schedule
(
    id        SERIAL PRIMARY KEY,
    file_name TEXT UNIQUE NOT NULL
);

ALTER TABLE meeting
    ADD CONSTRAINT conference_fk FOREIGN KEY (conference_id) REFERENCES conference (id);

ALTER TABLE conference
    ADD CONSTRAINT schedule_fk FOREIGN KEY (schedule_id) REFERENCES schedule (id);