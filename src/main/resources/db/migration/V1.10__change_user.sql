ALTER TABLE my_user
    DROP column firstname;

ALTER TABLE my_user
    DROP column lastname;

ALTER TABLE my_user
    ADD COLUMN lecturer_id INTEGER;

ALTER TABLE my_user
    ADD CONSTRAINT lecturer_fk FOREIGN KEY (lecturer_id) REFERENCES lecturer (id);