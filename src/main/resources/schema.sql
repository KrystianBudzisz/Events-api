DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS place;
CREATE TABLE place (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       code VARCHAR(255) NOT NULL UNIQUE,
                       capacity INT NOT NULL,
                       version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE event (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       date TIMESTAMP NOT NULL,
                       price DECIMAL(10, 2) NOT NULL,
                       number_of_people INT NOT NULL,
                       place_id INT,
                       version BIGINT NOT NULL DEFAULT 0,
                       FOREIGN KEY (place_id) REFERENCES place(id)
);
