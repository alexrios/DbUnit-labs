drop sequence person_seq if exists;
create sequence person_seq; 
create table person (
	id int,
	firstname varchar(255),
	lastname varchar(255),
	constraint person_pk primary key (id)
);