create database webapplication;
\c webapplication;

create table employe(
    id serial primary key,
    nom varchar(50),
    dateNaissance date,
    salaire decimal(18,2)
);

create table type (
    id serial primary key,
    type varchar(20)
);

create table routenational(
    id serial primary key,
    nom varchar(50),
    idType int ,
    foreign key (idType) references type(id) 
);

CREATE TABLE login(
    id SERIAL PRIMARY KEY,
    username VARCHAR(100),
    password VARCHAR(100),
    profile VARCHAR(50)
);

INSERT INTO login VALUES(default, 'Rakoto', '1234', 'admin');
INSERT INTO login VALUES(default, 'Rabe', 'qwerty', null);