CREATE TABLE excel
(
    id        SERIAL PRIMARY KEY,
    excelname TEXT UNIQUE NOT NULL,
    exceltype TEXT        NOT NULL,
    data      BYTEA        NOT NULL
);