INSERT INTO person(email, firstname, lastname)
VALUES ('admin@io.pl', 'Admin', 'Admin');

INSERT INTO my_user(person_id, password, role)
VALUES (1, '$argon2id$v=19$m=4096,t=3,p=1$p7AByGF2CfgDBmuTQzA7Fg$2OzCRRXBwNk6szcm+hZzDjNwq8EzkY9b+rXAvVcNqig','ADMIN')