-- Insert values into TYPEOF
INSERT INTO TYPEOF (reference, nodays, price) VALUES
('resident', 30, 50.00),
('tourist', 7, 20.00);

-- Insert values into PERSON
INSERT INTO PERSON (email, taxnumber, name) VALUES
('albino.jorge@exemplo.com', 123456789, 'Jorge Albino'),
('lou.teles@exemplo.com', 987654321, 'Lourenço Teles'),
('afonso_abranja@exemplo.com', 555444333, 'Afonso Abranja');
select * from person;

-- Insert values into CLIENT
INSERT INTO CLIENT (person, dtregister) VALUES
(1, '2023-01-01 10:00:00'),
(2, '2023-02-15 14:30:00');

-- Insert values into EMPLOYEE
INSERT INTO EMPLOYEE (person) VALUES
(3);

-- Insert values into CARD
INSERT INTO CARD (credit, typeof, client) VALUES
(20.00, 'resident', 1),
(50.00, 'tourist', 2);

-- Insert values into STATION
INSERT INTO STATION (latitude, longitude)
VALUES
    (37.7749, 12.4194),
    (34.0522, 18.2437),
    (40.7128, 74.0060);

-- Insert values into SCOOTERMODEL
INSERT INTO SCOOTERMODEL (designation, autonomy) VALUES
('Modelo A', 50),
('Modelo B', 70);

-- Insert values into SCOOTER
INSERT INTO SCOOTER (weight, maxvelocity, battery, model) VALUES
(12.5, 25.0, 80, 1),
(14.0, 30.0, 100, 2);

-- Insert values into DOCK
INSERT INTO DOCK (number, station, state, scooter) VALUES
(1, 1, 'occupy', 1),
(2, 1, 'free', NULL),
(1, 2, 'under maintenance', NULL);

-- Insert values into REPLACEMENTORDER
INSERT INTO REPLACEMENTORDER (dtorder, dtreplacement, roccupation, station) VALUES
('2023-03-01 09:00:00', '2023-03-02 12:00:00', 50, 1),
('2023-04-01 15:00:00', NULL, 75, 2);

-- Insert values into REPLACEMENT
INSERT INTO REPLACEMENT (number, dtreplacement, action, dtreporder, repstation, employee) VALUES
(1, '2023-03-02 13:00:00', 'inplace', '2023-03-01 09:00:00', 1, 3);

-- Insert values into TRAVEL
INSERT INTO TRAVEL (dtinitial, comment, evaluation, dtfinal, client, scooter, stinitial, stfinal) VALUES
('2023-05-01 08:00:00', 'Experiência incrível, gostaria bastante de repetir um dia.', 5, '2023-05-01 09:00:00', 1, 1, 1, 2),
('2023-05-02 10:30:00', NULL, NULL, NULL, 2, 2, 2, NULL);

-- Insert values into TOPUP
INSERT INTO TOPUP (dttopup, card, value) VALUES
('2023-06-01 11:00:00', 1, 20.00),
('2023-06-02 14:00:00', 2, 15.00);

-- Insert values into SERVICECOST
INSERT INTO SERVICECOST (unlock, usable) VALUES
(1.00, 0.15);
