-- my-test-data.sql

INSERT INTO agent (NAME, GENDER, CLEARANCE, BIRTH, DEATH) VALUES ('John Smith','male',5,'1969-01-01',NULL);

INSERT INTO agent (NAME, GENDER, CLEARANCE, BIRTH, DEATH) VALUES ('Dr. Zoidberg','something (maybe)',0,'2950-04-12','3015-07-25');

INSERT INTO agent (NAME, GENDER, CLEARANCE, BIRTH, DEATH) VALUES ('James Bond','male',10,'1989-05-12',NULL);

INSERT INTO mission (TITLE, COUNTRY, DESCRIPTION, COMPLETION, REQCLEARANCE, AGENT) VALUES ('Assassination', 'Oz', 'Kill the wizard as violently as possible', NULL, 1, NULL);

INSERT INTO mission (TITLE, COUNTRY, DESCRIPTION, COMPLETION, REQCLEARANCE, AGENT) VALUES ('Poisoning', 'Hungary', NULL, NULL, 1, 1);

INSERT INTO mission (TITLE, COUNTRY, DESCRIPTION, COMPLETION, REQCLEARANCE, AGENT) VALUES ('Hard Mission', 'Russia', 'really hard', NULL, 8, NULL);