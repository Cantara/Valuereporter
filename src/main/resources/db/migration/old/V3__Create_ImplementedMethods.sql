
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ImplementedMethod](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[prefix] [varchar](255) NOT NULL,
	[methodName] [varchar](255) NOT NULL
 CONSTRAINT [PK_ImplementedMethod] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

ALTER TABLE [dbo].[ImplementedMethod] ADD CONSTRAINT [IMK_PrefixMethodName] UNIQUE(prefix, methodName)

GO

SET ANSI_PADDING OFF
GO

insert into ImplementedMethod (prefix,methodName) values ('inital', 'com.valuereporter.test');