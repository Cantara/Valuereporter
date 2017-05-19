

CREATE TABLE ImplementedMethod(
	idserial PRIMARY KEY ,
	prefix varchar(255) NOT NULL,
	methodName varchar(255) NOT NULL
);

ALTER TABLE ImplementedMethod ADD CONSTRAINT IMK_PrefixMethodName UNIQUE(prefix, methodName);


insert into ImplementedMethod (prefix,methodName) values ('inital', 'com.valuereporter.test');