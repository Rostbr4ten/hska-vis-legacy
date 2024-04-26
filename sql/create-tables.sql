CREATE DATABASE webshop;

CREATE user 'webshopuser'@'%' IDENTIFIED BY '125ecf65adde7892bcd2655';

GRANT ALL on webshop.* to 'webshopuser'@'%';

USE webshop;

CREATE TABLE customer (
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	username VARCHAR(255) NOT NULL,
	role INT NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE role (
	id INT NOT NULL AUTO_INCREMENT,
	level1 INT,
	type VARCHAR(255),
	PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE UNIQUE INDEX UK_mufchskagt7e1w4ksmt9lum5l ON customer (username ASC);

CREATE INDEX FK74aoh99stptslhotgf41fitt0 ON customer (role ASC);

insert into `role` (`level1`, `type`) values(0, 'admin');
insert into `role` (`level1`, `type`) values(1, 'user');

insert into `customer` (`name`, `lastname`, `password`, `username`, `role`) values('admin', 'admin', 'admin', 'admin', 1);

create database category;

create user 'categoryUser'@'%' identified by '74523a732be65aff';

grant all on category.* to 'categoryUser'@'%';

USE category;

CREATE TABLE category (
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB;

create database product;

create user 'productUser'@'%' identified by 'ae4557bc8900ec';

grant all on product.* to 'productUser'@'%';

USE product;

CREATE TABLE product (
	id INT NOT NULL AUTO_INCREMENT,
	details VARCHAR(255),
	name VARCHAR(255),
	price DOUBLE,
	category_id INT,
	PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE INDEX FK1mtsbur82frn64de7balymq9s ON product (category_id ASC);