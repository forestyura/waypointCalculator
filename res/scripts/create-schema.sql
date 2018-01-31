--drop table DB_INFO;
--drop table FIELDS;
--drop table MACHINERY;
--drop table POINTS;

CREATE TABLE DB_INFO
	(NAME		TEXT    NOT NULL, 
	VAL			TEXT	NOT NULL,
	TS			TIMESTAMP);
	
CREATE TABLE FIELDS
	(ID 		INT		PRIMARY KEY,
	NAME		TEXT    NOT NULL);
	
CREATE TABLE MACHINERY
	(ID 		INT		PRIMARY KEY,
	NAME        TEXT    NOT NULL, 
	WORK_WIDTH	INT     NOT NULL, 
	FUEL        REAL	NOT NULL);

CREATE TABLE POINTS
	(ID 		INT		PRIMARY KEY,
	FIELD_ID	INT		NOT NULL,
	SEQ			INT 	NOT NULL,
	LAT			REAL    NOT NULL, 
	LON 		REAL    NOT NULL);
	
insert into DB_INFO VALUES("DB schema name", "1.2", datetime('now'));
insert into DB_INFO VALUES("FIELDS_SEQUENCE", "0", datetime('now'));
insert into DB_INFO VALUES("MACHINERY_SEQUENCE", "0", datetime('now'));
insert into DB_INFO VALUES("POINTS_SEQUENCE", "0", datetime('now'));