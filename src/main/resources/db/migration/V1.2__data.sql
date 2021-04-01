INSERT INTO person(email, firstname, lastname)
VALUES ('admin@io.pl', 'Admin', 'Admin');

INSERT INTO my_user(person_id, password, role)
VALUES (1, '$argon2id$v=19$m=4096,t=3,p=1$VnneknvRswBaxi0bjfnDwA$Vc0gLyMogWz7+66liWhGq+5G6/HJz5WaDwRYmpx5TR0','ROLE_ADMIN')