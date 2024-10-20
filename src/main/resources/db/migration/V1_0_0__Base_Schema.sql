create schema if not exists sales_management;

CREATE TABLE sales_management.seller (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     contact_info VARCHAR(255),
     registration_date TIMESTAMP NOT NULL
);

CREATE TABLE sales_management.transaction (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    payment_type VARCHAR(10) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_seller
      FOREIGN KEY (seller_id)
          REFERENCES sales_management.seller (id)
          ON DELETE CASCADE
);