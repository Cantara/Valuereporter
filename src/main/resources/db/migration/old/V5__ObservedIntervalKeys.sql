USE ValueReporter
GO

if not exists (select * from sysobjects where name='ObservedKeys' and xtype='U')
  BEGIN
    CREATE TABLE [dbo].[ObservedKeys](
      [id] [bigint] IDENTITY(1,1) PRIMARY KEY,
      [prefix] [varchar](255) NOT NULL,
      [methodName] [varchar](255) NOT NULL,
      CONSTRAINT AK_Prefix_MethodName UNIQUE (prefix, methodName)
    );
    insert into ObservedKeys(prefix, methodName) values ('initial', 'com.valuereporter.test');
  End

if not exists (select * from sysobjects where name='ObservedInterval' and xtype='U')
  BEGIN
    create table [dbo].[ObservedInterval] (
      [id] [bigint] IDENTITY(1,1) NOT NULL,
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
      select o.id, '2014-05-13T12:02:43.296', 15*60*1000, 4, 50, 5.0, 2,0,0
      from ObservedKeys o
      where prefix='initial' and methodName = 'com.valuereporter.test';
  END
