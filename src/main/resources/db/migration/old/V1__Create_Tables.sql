SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ObservedMethod](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[prefix] [varchar](255) NOT NULL,
	[methodName] [varchar](255) NOT NULL,
	[startTime] [datetime] NOT NULL,
	[endTime] [datetime] NOT NULL,
	[duration] [int] NOT NULL,
 CONSTRAINT [PK_ObservedMethod] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

insert into ObservedMethod (prefix,methodName, startTime, endTime, duration) values ('inital', 'com.valuereporter.test', '2014-05-13T12:02:43.296','2014-05-13T12:02:43.596',300);

