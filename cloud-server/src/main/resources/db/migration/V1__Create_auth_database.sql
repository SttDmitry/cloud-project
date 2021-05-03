CREATE TABLE auth_data (
    id serial NOT NULL,
    login varchar(255) NOT NULL,
    "password" varchar(255) NOT NULL,
    username varchar(255) NOT NULL,
    PRIMARY KEY (id)
);