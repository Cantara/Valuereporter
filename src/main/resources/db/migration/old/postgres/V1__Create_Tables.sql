

CREATE TABLE ObservedMethod (
	id  			serial PRIMARY KEY,
	prefix    VARCHAR(255) NOT NULL,
	methodName VARCHAR(255) NOT NULL,
	startTime DATETIME     NOT NULL,
	endTime   DATETIME     NOT NULL,
	duration  INT          NOT NULL
);

insert into ObservedMethod (prefix,methodName, startTime, endTime, duration) values ('inital', 'com.valuereporter.test', '2014-05-13 12:02:43.296','2014-05-13 12:02:43.596',300);

