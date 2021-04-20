ALTER TABLE excel
    ADD column schedule_id INTEGER;

ALTER TABLE excel
    ADD CONSTRAINT schedule_fk FOREIGN KEY (schedule_id) REFERENCES schedule (id);