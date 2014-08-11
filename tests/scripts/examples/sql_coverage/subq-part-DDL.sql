-- DDL for subqueries table partitioned on varchar column

CREATE TABLE P1 (
  ID INTEGER NOT NULL,
  DESC VARCHAR(15) NOT NULL,
  NUM INTEGER,
  RATIO FLOAT,
  PRIMARY KEY (ID, DESC)
);
PARTITION TABLE P1 ON COLUMN DESC;

CREATE TABLE P2 (
  ID INTEGER NOT NULL,
  DESC VARCHAR(15) NOT NULL,
  NUM INTEGER,
  RATIO FLOAT,
  PRIMARY KEY (ID, DESC)
);
PARTITION TABLE P2 ON COLUMN DESC;

CREATE TABLE R1 (
  ID INTEGER NOT NULL,
  DESC VARCHAR(15),
  NUM INTEGER,
  RATIO FLOAT,
  PRIMARY KEY (ID)
);

CREATE TABLE R2 (
  ID INTEGER NOT NULL,
  DESC VARCHAR(15),
  NUM INTEGER,
  RATIO FLOAT,
  PRIMARY KEY (ID)
);