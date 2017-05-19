use ValueReporter;
CREATE TABLE IF NOT EXISTS userLogon (
  id bigint auto_increment primary key,
  starttime TIMESTAMP  NOT NULL,
  userid varchar(255)  NOT NULL
);

CREATE TABLE IF NOT EXISTS userSession (
  id bigint auto_increment primary key,
  starttime TIMESTAMP  NOT NULL,
  userid varchar(255)  NOT NULL,
  usersessionfunction varchar(255),
  applicationtokenid varchar(255)  NOT NULL,
  applicationid varchar(255)  NOT NULL
);