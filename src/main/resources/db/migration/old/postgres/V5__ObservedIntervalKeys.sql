    CREATE TABLE ObservedKeys(
      id serial PRIMARY KEY,
      prefix varchar(255) NOT NULL,
      methodName varchar(255) NOT NULL,
      CONSTRAINT AK_Prefix_MethodName UNIQUE (prefix, methodName)
    );
    insert into ObservedKeys(prefix, methodName) values ('initial', 'com.valuereporter.test');
 

    create table ObservedInterval (
      id serial PRIMARY KEY ,
      observedKeysId bigint NOT NULL ,
      startTime datetime NOT NULL,
      duration bigint not null,
      vrCount bigint NOT NULL,
      vrMax bigint not null,
      vrMin bigint not null,
      vrMean decimal not null,
      vrMedian decimal not null,
      stdDev decimal not null,
      p95 decimal,
      p98 decimal,
      p99 decimal,
      FOREIGN KEY (observedKeysId) REFERENCES ObservedKeys(id)
    );
    insert into ObservedInterval (observedKeysId, startTime, duration, vrCount, vrMax, vrMean,vrMin,vrMedian, stdDev)
      select o.id, '2014-05-13 12:02:43.296', 15*60*1000, 4, 50, 5.0, 2,0,0
      from ObservedKeys o
      where prefix='initial' and methodName = 'com.valuereporter.test';
