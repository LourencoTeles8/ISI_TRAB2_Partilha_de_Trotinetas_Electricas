-- Create TYPEOF table
CREATE TABLE TYPEOF (
    reference CHAR(10) PRIMARY KEY CHECK (reference IN ('resident','tourist')),
    nodays INTEGER NOT NULL CHECK (nodays > 0),
    price NUMERIC(4,2) NOT NULL CHECK (price > 0)
);

-- Create PERSON table
CREATE TABLE PERSON (
    id SERIAL PRIMARY KEY,
    email VARCHAR(40) NOT NULL UNIQUE CHECK (POSITION('@' IN email) > 0),
    taxnumber INTEGER NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL
);

-- Create CLIENT table
CREATE TABLE CLIENT (
    person INTEGER PRIMARY KEY REFERENCES PERSON(id),
    dtregister TIMESTAMP NOT NULL
);

-- Create EMPLOYEE table
CREATE TABLE EMPLOYEE (
    number SERIAL NOT NULL UNIQUE,
    person INTEGER PRIMARY KEY REFERENCES PERSON(id)
);

-- Create CARD table
CREATE TABLE CARD (
    id SERIAL PRIMARY KEY,
    credit NUMERIC(4,2) NOT NULL CHECK (credit >= 0),
    typeof CHAR(10) NOT NULL REFERENCES TYPEOF(reference),
    client INTEGER NOT NULL REFERENCES CLIENT(person)
);

-- Create STATION table
CREATE TABLE STATION (
    id SERIAL PRIMARY KEY,
    latitude NUMERIC(6,4) NOT NULL,
    longitude NUMERIC(6,4) NOT NULL
);

-- Create SCOOTERMODEL table
CREATE TABLE SCOOTERMODEL (
    number SERIAL PRIMARY KEY,
    designation VARCHAR(30) NOT NULL,
    autonomy INTEGER NOT NULL CHECK (autonomy > 0)
);

-- Create SCOOTER table
CREATE TABLE SCOOTER (
    id SERIAL PRIMARY KEY,
    weight NUMERIC(4,2) NOT NULL CHECK (weight > 0),
    maxvelocity NUMERIC(4,2) NOT NULL CHECK (maxvelocity > 0),
    battery INTEGER NOT NULL CHECK (battery > 0),
    model INTEGER NOT NULL REFERENCES SCOOTERMODEL(number)
);

-- Create DOCK table
CREATE TABLE DOCK (
    number SERIAL,
    station INTEGER NOT NULL REFERENCES STATION(id),
    state VARCHAR(30) NOT NULL CHECK (state IN ('free', 'occupy', 'under maintenance')),
    scooter INTEGER REFERENCES SCOOTER(id),
    primary key(number,station),
    CONSTRAINT dock_state_constraint CHECK (state != 'occupy' OR scooter IS NOT NULL)
);

-- Create REPLACEMENTORDER table
CREATE TABLE REPLACEMENTORDER (
    dtorder TIMESTAMP UNIQUE,
    dtreplacement TIMESTAMP CHECK (dtreplacement IS NULL OR dtreplacement > dtorder),
    roccupation INTEGER NOT NULL CHECK (roccupation BETWEEN 0 AND 100),
    station INTEGER UNIQUE NOT NULL REFERENCES STATION(id),
    primary key (dtorder,station)
);

-- Create REPLACEMENT table
CREATE TABLE REPLACEMENT (
    number SERIAL,
    dtreplacement TIMESTAMP NOT NULL CHECK (dtreplacement > dtreporder),
    action CHAR(8) NOT NULL CHECK (action IN ('inplace', 'remove')),
    dtreporder TIMESTAMP NOT NULL REFERENCES REPLACEMENTORDER(dtorder) ,
    repstation INTEGER NOT NULL REFERENCES REPLACEMENTORDER(station),
    employee INTEGER NOT NULL REFERENCES EMPLOYEE(person),
    PRIMARY KEY (number,dtreporder,repstation)
);

-- Create TRAVEL table
CREATE TABLE TRAVEL (
    dtinitial TIMESTAMP,
    comment VARCHAR(100),
    evaluation INTEGER CHECK (evaluation IS NULL OR evaluation BETWEEN 1 AND 5),
    dtfinal TIMESTAMP CHECK (dtfinal IS NULL OR dtfinal > dtinitial),
    client INTEGER NOT NULL REFERENCES CLIENT(person),
    scooter INTEGER NOT NULL REFERENCES SCOOTER(id),
    stinitial INTEGER NOT NULL REFERENCES STATION(id),
    stfinal INTEGER REFERENCES STATION(id),
    CONSTRAINT travel_comment_evaluation CHECK (comment IS NULL OR evaluation IS NOT NULL),
    primary key (dtinitial,client)
);

-- Create TOPUP table
CREATE TABLE TOPUP (
    dttopup TIMESTAMP,
    card INTEGER NOT NULL REFERENCES CARD(id),
    value NUMERIC(4,2) NOT NULL CHECK (value > 0),
    PRIMARY key (dttopup,card)
);

-- Create SERVICECOST table
CREATE TABLE SERVICECOST (
    unlock NUMERIC(3,2) NOT NULL CHECK (unlock = 1.00),
    usable NUMERIC(3,2) NOT NULL CHECK (usable = 0.15)
);

select  * from person ;

select  * from client ;
