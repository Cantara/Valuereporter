use valuereporter;
create table if not exists ObservedMethod (
  id bigint auto_increment primary key,
  serviceName varchar(255) NOT NULL,
  methodName varchar(255) NOT NULL,
  startTime datetime NOT NULL,
  endTime datetime NOT NULL,
  duration int NOT NULL
) engine=InnoDB default charset latin1;


insert into ObservedMethod (serviceName,methodName, startTime, endTime, duration) values ('inital', 'com.valuereporter.test', '2014-05-13T12:02:43.296','2014-05-13T12:02:43.596',300);

create table if not exists ObservedKeys (
  id bigint auto_increment primary key ,
  serviceName varchar(255) NOT NULL,
  methodName varchar(255) NOT NULL,
  UNIQUE serviceName_name(`serviceName`, `methodName`)
);

insert into ObservedKeys(serviceName, methodName) value ('initial', 'com.valuereporter.test');

create table if not exists ObservedInterval (
  id bigint auto_increment primary key,
  observedKeysId bigint NOT NULL ,
  startTime datetime NOT NULL,
  duration bigint not null,
  vrcount bigint NOT NULL,
  vrMax bigint not null,
  vrMin bigint not null,
  vrMean decimal not null,
  vrMedian decimal not null,
  stdDev decimal not null,
  p95 decimal,
  p98 decimal,
  p99 decimal,
  FOREIGN KEY (observedKeysId) REFERENCES ObservedKeys(id)
  #t,count,max,mean,min,stddev,p50,p75,p95,p98,p99,p999,mean_rate,m1_rate,m5_rate,m15_rate,rate_unit,duration_unit
);

insert into ObservedInterval (observedKeysId, startTime, duration, vrCount, vrMax, vrMean,vrMin,vrMedian, stdDev)
  select o.id, '2014-05-13T12:02:43.296', 15*60*1000, 4, 50, 5.0, 2,0,0
  from ObservedKeys o
  where serviceName='initial' and methodName = 'com.valuereporter.test';