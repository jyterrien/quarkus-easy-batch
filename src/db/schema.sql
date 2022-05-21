DROP TABLE IF EXISTS tweet;

CREATE TABLE tweet (
  "id" integer NOT NULL,
  "user" varchar(32) NOT NULL,
  "message" varchar(280) NOT NULL
);