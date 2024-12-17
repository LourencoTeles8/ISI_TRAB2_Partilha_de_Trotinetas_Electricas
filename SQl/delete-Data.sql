-- Delete from dependent (child) tables
DELETE FROM TRAVEL;
DELETE FROM TOPUP;
DELETE FROM REPLACEMENT;
DELETE FROM REPLACEMENTORDER;
DELETE FROM DOCK;
DELETE FROM SCOOTER;
DELETE FROM CARD;
DELETE FROM CLIENT;
DELETE FROM EMPLOYEE;

-- Delete from independent (parent) tables
DELETE FROM SCOOTERMODEL;
DELETE FROM STATION;
DELETE FROM TYPEOF;
DELETE FROM PERSON;

-- Reset sequences for all tables with SERIAL columns
ALTER SEQUENCE person_id_seq RESTART WITH 1;
ALTER SEQUENCE employee_number_seq RESTART WITH 1;
ALTER SEQUENCE card_id_seq RESTART WITH 1;
ALTER SEQUENCE station_id_seq RESTART WITH 1;
ALTER SEQUENCE scootermodel_number_seq RESTART WITH 1;
ALTER SEQUENCE scooter_id_seq RESTART WITH 1;
ALTER SEQUENCE dock_number_seq RESTART WITH 1;
ALTER SEQUENCE replacement_number_seq RESTART WITH 1;

-- Note: SERVICECOST and TYPEOF tables don't use SERIAL, so no sequence reset is needed.