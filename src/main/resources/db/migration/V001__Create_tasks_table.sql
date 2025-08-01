CREATE TABLE tasks (
                     id            SERIAL PRIMARY KEY,
                     title         VARCHAR NOT NULL,
                     description   VARCHAR,
                     status        VARCHAR NOT NULL,
                     due_date      TIMESTAMP DEFAULT now(),
                     created_date  TIMESTAMP DEFAULT now()
);

