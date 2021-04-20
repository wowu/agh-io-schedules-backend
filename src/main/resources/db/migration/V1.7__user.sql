INSERT INTO my_user(email, firstname, lastname, password, role)
VALUES ('franekdzbanek@io.pl', 'Franek', 'Dzbanek',
        '$argon2id$v=19$m=4096,t=3,p=1$p7AByGF2CfgDBmuTQzA7Fg$2OzCRRXBwNk6szcm+hZzDjNwq8EzkY9b+rXAvVcNqig', 'LECTURER');

INSERT INTO my_user(email, firstname, lastname, password, role)
VALUES ('idontexist@io.pl', 'xyz', 'abc',
        '$argon2id$v=19$m=4096,t=3,p=1$p7AByGF2CfgDBmuTQzA7Fg$2OzCRRXBwNk6szcm+hZzDjNwq8EzkY9b+rXAvVcNqig', 'LECTURER');