CREATE TABLE email
(
    id    SERIAL PRIMARY KEY,
    email TEXT UNIQUE NOT NULL
);

CREATE TABLE my_user
(
    id       SERIAL PRIMARY KEY,
    email_id INTEGER UNIQUE NOT NULL,
    password TEXT           NOT NULL,
    role     TEXT           NOT NULL
);

ALTER TABLE my_user
    ADD CONSTRAINT email_fk FOREIGN KEY (email_id) REFERENCES email (id);

CREATE TABLE schedule
(
    id          SERIAL PRIMARY KEY,
    file_name   TEXT UNIQUE NOT NULL,
    public_link TEXT UNIQUE NOT NULL,
    description TEXT        NOT NULL
);

CREATE table subscription
(
    id          SERIAL PRIMARY KEY,
    email_id    INTEGER NOT NULL,
    schedule_id INTEGER NOT NULL,
    active      BOOLEAN NOT NULL
);

ALTER TABLE subscription
    ADD CONSTRAINT schedule_fk FOREIGN KEY (schedule_id) REFERENCES schedule (id);

ALTER TABLE subscription
    ADD CONSTRAINT email_fk FOREIGN KEY (email_id) REFERENCES email (id);

CREATE TABLE lecturer
(
    id                 SERIAL PRIMARY KEY,
    email_id           INTEGER UNIQUE NOT NULL,
    name               TEXT           NOT NULL,
    surname            TEXT           NOT NULL,
    active_subscription BOOLEAN        NOT NULL
);

ALTER TABLE lecturer
    ADD CONSTRAINT email_fk FOREIGN KEY (email_id) REFERENCES email (id);

CREATE TABLE conference
(
    id          SERIAL PRIMARY KEY,
    schedule_id INTEGER NOT NULL
);

ALTER TABLE conference
    ADD CONSTRAINT schedule_fk FOREIGN KEY (schedule_id) REFERENCES schedule (id);

CREATE TABLE meeting
(
    id               SERIAL PRIMARY KEY,
    conference_id    INTEGER   NOT NULL,
    date_start       TIMESTAMP NOT NULL,
    date_end         TIMESTAMP NOT NULL,
    subject          TEXT      NOT NULL,
    group_name       TEXT      NOT NULL,
    lecturer_name    TEXT      NOT NULL,
    lecturer_surname TEXT      NOT NULL,
    type             TEXT      NOT NULL,
    length_in_hours  INTEGER   NOT NULL,
    format           TEXT      NOT NULL,
    room             TEXT      NOT NULL
);

ALTER TABLE meeting
    ADD CONSTRAINT conference_fk FOREIGN KEY (conference_id) REFERENCES conference (id);

CREATE TABLE excel
(
    id          SERIAL PRIMARY KEY,
    excel_name  TEXT UNIQUE    NOT NULL,
    excel_type  TEXT           NOT NULL,
    data        BYTEA          NOT NULL,
    schedule_id INTEGER UNIQUE NOT NULL
);

ALTER TABLE excel
    ADD CONSTRAINT schedule_fk FOREIGN KEY (schedule_id) REFERENCES schedule (id);