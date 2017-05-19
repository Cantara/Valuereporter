
USE [ValueReporter]
GO

/****** Object:  Index [methodName]    Script Date: 06/10/2014 12:42:22 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[ObservedMethod]') AND name = N'methodName')
DROP INDEX [methodName] ON [dbo].[ObservedMethod] WITH ( ONLINE = OFF )
GO



/****** Object:  Index [PK_ObservedMethod]    Script Date: 06/10/2014 12:40:07 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[ObservedMethod]') AND name = N'PK_ObservedMethod')
ALTER TABLE [dbo].[ObservedMethod] DROP CONSTRAINT [PK_ObservedMethod]
GO



ALTER TABLE ObservedMethod
ALTER COLUMN prefix varchar(255) NOT NULL

ALTER TABLE ObservedMethod
ALTER COLUMN methodName varchar(255) NOT NULL
                                     
USE [ValueReporter]
GO

/****** Object:  Index [PK_ObservedMethod]    Script Date: 06/10/2014 12:39:42 ******/
ALTER TABLE [dbo].[ObservedMethod] ADD  CONSTRAINT [PK_ObservedMethod] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

USE [ValueReporter]
GO

/****** Object:  Index [methodName]    Script Date: 06/10/2014 12:42:01 ******/
CREATE NONCLUSTERED INDEX [methodName] ON [dbo].[ObservedMethod] 
(
	[methodName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO