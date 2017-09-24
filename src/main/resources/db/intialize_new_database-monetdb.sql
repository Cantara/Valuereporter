---------------------------------------------------------------
--- Run using user with system admin privileges.            ---
--- Will create new database, two logins and two users.     ---
---------------------------------------------------------------

CREATE USER "vrAdmin" WITH PASSWORD 'vrAdmin' NAME 'Valuereporter Admin' SCHEMA sys;
CREATE SCHEMA valuereporter AUTHORIZATION vrAdmin;
ALTER USER vrAdmin SET SCHEMA valuereporter;

CREATE TABLE observedmethod (
	id  			BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	serviceName    VARCHAR(255) NOT NULL,
	methodname VARCHAR(255) NOT NULL,
	starttime timestamp NOT NULL,
	endtime   timestamp NOT NULL,
	duration  integer NOT NULL
);

insert into observedmethod (serviceName,methodname, starttime, endtime, duration) values ('inital', 'com.valuereporter.test', '2014-05-13 12:02:43.296','2014-05-13 12:02:43.596','300');

CREATE TABLE implementedmethod(
	id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	serviceName varchar(255) NOT NULL,
	methodname varchar(255) NOT NULL
);
ALTER TABLE implementedmethod ADD CONSTRAINT IMK_PrefixMethodName2 UNIQUE (serviceName, methodname);
insert into implementedmethod (serviceName,methodname) values ('inital', 'com.valuereporter.test');
