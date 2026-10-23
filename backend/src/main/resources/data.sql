-- Demo Data for MySQL
INSERT INTO items (name, category, expiry_date, reminder_days, owner_name, estimated_value, notes)
SELECT 'Paracetamol 500mg', 'MEDICINE', '2023-12-01', 7, 'Self', 150.0, 'Emergency fever pills'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = 'Paracetamol 500mg');

INSERT INTO items (name, category, expiry_date, reminder_days, owner_name, estimated_value, notes)
SELECT 'Organic Milk', 'GROCERY', DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), 3, 'Kitchen', 80.0, 'Buy 2 packets next time'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = 'Organic Milk');

INSERT INTO items (name, category, expiry_date, reminder_days, owner_name, estimated_value, notes)
SELECT 'Car Health Insurance', 'INSURANCE', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), 15, 'Dad', 12500.0, 'HDFC ERGO Policy'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = 'Car Health Insurance');

INSERT INTO items (name, category, expiry_date, reminder_days, owner_name, estimated_value, notes)
SELECT 'Vehicle Pollution Certificate', 'VEHICLE_DOCUMENT', DATE_ADD(CURRENT_DATE, INTERVAL 12 DAY), 10, 'Self', 500.0, 'Valid across India'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = 'Vehicle Pollution Certificate');

INSERT INTO items (name, category, expiry_date, reminder_days, owner_name, estimated_value, notes)
SELECT 'Dell Laptop Warranty', 'WARRANTY', DATE_ADD(CURRENT_DATE, INTERVAL 45 DAY), 14, 'Self', 65000.0, 'Premium Support Care'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = 'Dell Laptop Warranty');
