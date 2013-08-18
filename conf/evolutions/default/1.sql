

# Tasks schema
 
# --- !Ups

CREATE SEQUENCE user_id_seq;
CREATE TABLE users{
    userId integer NOT NULL DEFAULT nextval('user_id_seq'),
    name varchar (255)
);
CREATE SEQUENCE excel_id_seq;
CREATE TABLE excels{
    excelId integer NOT NULL DEFAULT nextval('excel_id_seq'),
    path varchar (255)
);
 
# --- !Downs
 
DROP TABLE excels
DROP SEQUENCE excel_id_seq
DROP TABLE users
DROP SEQUENCE user_id_seq

