INSERT INTO email(email)
VALUES ('admin@io.pl');

INSERT INTO email(email)
VALUES ('franekdzbanek@io.pl');

INSERT INTO email(email)
VALUES ('idontexist@io.pl');

INSERT INTO my_user(email_id, password, role)
VALUES (1, '$argon2id$v=19$m=4096,t=3,p=1$p7AByGF2CfgDBmuTQzA7Fg$2OzCRRXBwNk6szcm+hZzDjNwq8EzkY9b+rXAvVcNqig', 'ADMIN');

INSERT INTO my_user(email_id, password, role)
VALUES (2, '$argon2id$v=19$m=4096,t=3,p=1$p7AByGF2CfgDBmuTQzA7Fg$2OzCRRXBwNk6szcm+hZzDjNwq8EzkY9b+rXAvVcNqig',
        'LECTURER');

INSERT INTO my_user(email_id, password, role)
VALUES (3, '$argon2id$v=19$m=4096,t=3,p=1$p7AByGF2CfgDBmuTQzA7Fg$2OzCRRXBwNk6szcm+hZzDjNwq8EzkY9b+rXAvVcNqig',
        'LECTURER');