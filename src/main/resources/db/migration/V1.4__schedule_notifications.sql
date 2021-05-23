ALTER TABLE schedule
ADD COLUMN notifications BOOLEAN;

ALTER TABLE lecturer
DROP COLUMN active_subscription;
