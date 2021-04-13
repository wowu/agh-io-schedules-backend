CREATE TABLE excel
(
    id        SERIAL PRIMARY KEY,
    excel_name TEXT UNIQUE NOT NULL,
    excel_type TEXT        NOT NULL,
    data      BYTEA        NOT NULL
);