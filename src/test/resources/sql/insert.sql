insert into sales_management.seller (id,name, contact_info, registration_date)
values
    (1,'Alberto Mayert', '878-999-0161', '2022-10-22 14:30:00'),
    (2,'Elmer Runte', '645-423-7550', '2024-09-03 09:45:00'),
    (3,'Christina Zieme', '921-270-2943', '2024-10-19 12:00:00');
INSERT INTO sales_management.transaction (id,seller_id, amount, payment_type, transaction_date)
VALUES
    (1,1, 500.12, 'TRANSFER', '2024-10-22 14:30:00'),
    (2,1, 100.50, 'CARD', '2024-10-22 15:00:00'),
    (3,1, 325.51, 'TRANSFER', '2024-10-22 14:45:00'),
    (4,2, 12.53, 'CARD', '2024-09-03 10:00:00'),
    (5,2, 52.78, 'CASH', '2024-09-03 11:00:00'),
    (6,3, 5.61, 'CASH', '2024-10-19 12:30:00');