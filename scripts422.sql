-- Таблица "Человек"
CREATE TABLE person (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        age INT,
                        has_driver_license BOOLEAN
);

-- Таблица "Машина"
CREATE TABLE car (
                     id SERIAL PRIMARY KEY,
                     brand VARCHAR(255) NOT NULL,
                     model VARCHAR(255) NOT NULL,
                     price NUMERIC(10, 2)
);

-- Связующая таблица "Человек-Машина" (многие-ко-многим)
CREATE TABLE person_car (
                            person_id INT REFERENCES person(id),
                            car_id INT REFERENCES car(id),
                            PRIMARY KEY (person_id, car_id)
);