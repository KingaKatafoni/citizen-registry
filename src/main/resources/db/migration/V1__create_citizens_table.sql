CREATE TABLE citizens
(
    id               BIGSERIAL PRIMARY KEY,
    pesel            VARCHAR(11)  NOT NULL UNIQUE,
    first_name       VARCHAR(50)  NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    date_of_birth    DATE         NOT NULL,
    gender           VARCHAR(10)  NOT NULL,
    email            VARCHAR(255),
    phone_number     VARCHAR(20),
    street           VARCHAR(255),
    building_number  VARCHAR(10),
    apartment_number VARCHAR(10),
    city             VARCHAR(100),
    zip_code         VARCHAR(10),
    voivodeship      VARCHAR(50),
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);