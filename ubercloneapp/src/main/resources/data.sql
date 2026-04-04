-- Users with firstName, lastName, email, password, phoneNumber
INSERT INTO app_user (id, firstName, lastName, email, password, phoneNumber, active) VALUES
(1, 'Aarav', 'Sharma', 'aarav@gmail.com', 'Password', '+91-9876543210', true),
(2, 'Vivaan', 'Khanna', 'vivaan.khanna@example.com', '$2a$10$examplePasswordHash2', '+91-9876543211', true),
(3, 'Aditya', 'Verma', 'aditya.verma@example.com', '$2a$10$examplePasswordHash3', '+91-9876543212', true),
(4, 'Vihaan', 'Kapoor', 'vihaan.kapoor@example.com', '$2a$10$examplePasswordHash4', '+91-9876543213', true),
(5, 'Arjun', 'Patel', 'arjun.patel@example.com', '$2a$10$examplePasswordHash5', '+91-9876543214', true),
(6, 'Sai', 'Reddy', 'sai.reddy@example.com', '$2a$10$examplePasswordHash6', '+91-9876543215', true),
(7, 'Ananya', 'Nair', 'ananya.nair@example.com', '$2a$10$examplePasswordHash7', '+91-9876543216', true),
(8, 'Ishaan', 'Thakur', 'ishaan.thakur@example.com', '$2a$10$examplePasswordHash8', '+91-9876543217', true),
(9, 'Aryan', 'Rao', 'aryan.rao@example.com', '$2a$10$examplePasswordHash9', '+91-9876543218', true),
(10, 'Diya', 'Singh', 'diya.singh@example.com', '$2a$10$examplePasswordHash10', '+91-9876543219', true),
(11, 'Kabir', 'Joshi', 'kabir.joshi@example.com', '$2a$10$examplePasswordHash11', '+91-9876543220', true),
(12, 'Krishna', 'Iyer', 'krishna.iyer@example.com', '$2a$10$examplePasswordHash12', '+91-9876543221', true),
(13, 'Reyansh', 'Pandey', 'reyansh.pandey@example.com', '$2a$10$examplePasswordHash13', '+91-9876543222', true),
(14, 'Ayaan', 'Mehta', 'ayaan.mehta@example.com', '$2a$10$examplePasswordHash14', '+91-9876543223', true),
(15, 'Anaya', 'Mishra', 'anaya.mishra@example.com', '$2a$10$examplePasswordHash15', '+91-9876543224', true),
(16, 'Navya', 'Kulkarni', 'navya.kulkarni@example.com', '$2a$10$examplePasswordHash16', '+91-9876543225', true),
(17, 'Naira', 'Desai', 'naira.desai@example.com', '$2a$10$examplePasswordHash17', '+91-9876543226', true),
(18, 'Dhruv', 'Saxena', 'dhruv.saxena@example.com', '$2a$10$examplePasswordHash18', '+91-9876543227', true),
(19, 'Sara', 'Bajaj', 'sara.bajaj@example.com', '$2a$10$examplePasswordHash19', '+91-9876543228', true),
(20, 'Arnav', 'Malhotra', 'arnav.malhotra@example.com', '$2a$10$examplePasswordHash20', '+91-9876543229', true),
(41, 'Admin', 'User', 'admin@uberclone.com', '$2a$10$adminPasswordHash', '+91-9999999999', true),
(42, 'Super', 'Admin', 'superadmin@uberclone.com', '$2a$10$superAdminHash', '+91-9999999998', true);

-- User Roles: RIDER, DRIVER, ADMIN
INSERT INTO user_roles (user_id, roles) VALUES
(1, 'RIDER'),
(2, 'RIDER'),
(2, 'DRIVER'),
(3, 'RIDER'),
(3, 'DRIVER'),
(4, 'RIDER'),
(4, 'DRIVER'),
(5, 'RIDER'),
(5, 'DRIVER'),
(6, 'RIDER'),
(6, 'DRIVER'),
(7, 'RIDER'),
(7, 'DRIVER'),
(8, 'RIDER'),
(8, 'DRIVER'),
(9, 'RIDER'),
(9, 'DRIVER'),
(10, 'RIDER'),
(10, 'DRIVER'),
(11, 'RIDER'),
(11, 'DRIVER'),
(12, 'RIDER'),
(12, 'DRIVER'),
(13, 'RIDER'),
(13, 'DRIVER'),
(14, 'RIDER'),
(14, 'DRIVER'),
(15, 'RIDER'),
(15, 'DRIVER'),
(16, 'RIDER'),
(16, 'DRIVER'),
(17, 'RIDER'),
(17, 'DRIVER'),
(18, 'RIDER'),
(18, 'DRIVER'),
(19, 'RIDER'),
(19, 'DRIVER'),
(20, 'RIDER'),
(20, 'DRIVER'),
(41, 'ADMIN'),
(42, 'ADMIN');

-- Riders with ratings
INSERT INTO rider (id, user_id, rating) VALUES
(1, 1, 4.9),
(2, 3, 4.7),
(3, 4, 4.5),
(4, 5, 4.8),
(5, 6, 4.6),
(6, 7, 4.4),
(7, 8, 4.9),
(8, 9, 4.3),
(9, 10, 4.7),
(10, 11, 4.5),
(11, 12, 4.8),
(12, 13, 4.6),
(13, 14, 4.9),
(14, 15, 4.4),
(15, 16, 4.7),
(16, 17, 4.5),
(17, 18, 4.8),
(18, 19, 4.6),
(19, 20, 4.9),
(20, 2, 4.5);

-- Drivers with vehicleId, rating (4.2-4.9), available, current_location
INSERT INTO driver (id, user_id, vehicle_id, rating, available, active, current_location) VALUES
(2, 2, 'DL01AB1234', 4.7, true, true, ST_GeomFromText('POINT(77.1025 28.7041)', 4326)),
(3, 3, 'DL01CD5678', 4.8, true, true, ST_GeomFromText('POINT(77.2167 28.6667)', 4326)),
(4, 4, 'DL01EF9012', 4.6, true, true, ST_GeomFromText('POINT(77.2273 28.6353)', 4326)),
(5, 5, 'DL01GH3456', 4.9, true, true, ST_GeomFromText('POINT(77.2500 28.5500)', 4326)),
(6, 6, 'DL01IJ7890', 4.3, true, true, ST_GeomFromText('POINT(77.2000 28.6200)', 4326)),
(7, 7, 'DL01KL1234', 4.4, true, true, ST_GeomFromText('POINT(77.2800 28.5900)', 4326)),
(8, 8, 'DL01MN5678', 4.5, true, true, ST_GeomFromText('POINT(77.2600 28.6800)', 4326)),
(9, 9, 'DL01OP9012', 4.6, true, true, ST_GeomFromText('POINT(77.2200 28.6400)', 4326)),
(10, 10, 'DL01QR3456', 4.7, true, true, ST_GeomFromText('POINT(77.2700 28.6700)', 4326)),
(11, 11, 'DL01ST7890', 4.8, true, true, ST_GeomFromText('POINT(77.2400 28.6100)', 4326)),
(12, 12, 'DL01UV1234', 4.2, true, true, ST_GeomFromText('POINT(77.2300 28.6600)', 4326)),
(13, 13, 'DL01WX5678', 4.1, true, true, ST_GeomFromText('POINT(77.1900 28.6000)', 4326)),
(14, 14, 'DL01YZ9012', 4.0, false, true, ST_GeomFromText('POINT(77.1800 28.6300)', 4326)),
(15, 15, 'DL02AB3456', 4.5, true, true, ST_GeomFromText('POINT(77.1500 28.6500)', 4326)),
(16, 16, 'DL02CD7890', 4.3, true, true, ST_GeomFromText('POINT(77.1200 28.6700)', 4326)),
(17, 17, 'DL02EF1234', 4.6, true, true, ST_GeomFromText('POINT(77.1700 28.6800)', 4326)),
(18, 18, 'DL02GH5678', 4.4, true, true, ST_GeomFromText('POINT(77.1400 28.6900)', 4326)),
(19, 19, 'DL02IJ9012', 4.7, true, true, ST_GeomFromText('POINT(77.1300 28.7000)', 4326)),
(20, 20, 'DL02KL3456', 4.8, true, true, ST_GeomFromText('POINT(77.1100 28.7100)', 4326));

-- Wallets with balances for all users
INSERT INTO wallet (id, user_id, balance) VALUES
(1, 1, 500.00),
(2, 2, 750.00),
(3, 3, 300.00),
(4, 4, 450.00),
(5, 5, 600.00),
(6, 6, 200.00),
(7, 7, 550.00),
(8, 8, 400.00),
(9, 9, 350.00),
(10, 10, 800.00),
(11, 11, 500.00),
(12, 12, 650.00),
(13, 13, 250.00),
(14, 14, 450.00),
(15, 15, 550.00),
(16, 16, 700.00),
(17, 17, 400.00),
(18, 18, 600.00),
(19, 19, 350.00),
(20, 20, 500.00),
(21, 41, 0.00),
(22, 42, 0.00);

-- Rides with different statuses: COMPLETED, CANCELLED, ONGOING
-- COMPLETED rides
INSERT INTO ride (id, rider_id, driver_id, pickup_location, drop_off_location, fare, otp, payment_method, ride_status, created_time, started_at, ended_at) VALUES
(1, 1, 2, ST_GeomFromText('POINT(77.1025 28.7041)', 4326), ST_GeomFromText('POINT(77.2167 28.6667)', 4326), 245.50, '1234', 'WALLET', 'COMPLETED', '2026-04-01 10:00:00', '2026-04-01 10:15:00', '2026-04-01 10:45:00'),
(2, 2, 3, ST_GeomFromText('POINT(77.2167 28.6667)', 4326), ST_GeomFromText('POINT(77.2273 28.6353)', 4326), 180.00, '5678', 'CASH', 'COMPLETED', '2026-04-01 11:30:00', '2026-04-01 11:45:00', '2026-04-01 12:15:00'),
(3, 3, 5, ST_GeomFromText('POINT(77.2273 28.6353)', 4326), ST_GeomFromText('POINT(77.2500 28.5500)', 4326), 320.75, '9012', 'WALLET', 'COMPLETED', '2026-04-02 09:00:00', '2026-04-02 09:20:00', '2026-04-02 10:00:00'),
(4, 4, 6, ST_GeomFromText('POINT(77.2000 28.6200)', 4326), ST_GeomFromText('POINT(77.2800 28.5900)', 4326), 195.25, '3456', 'WALLET', 'COMPLETED', '2026-04-02 14:00:00', '2026-04-02 14:10:00', '2026-04-02 14:40:00'),
(5, 5, 7, ST_GeomFromText('POINT(77.2600 28.6800)', 4326), ST_GeomFromText('POINT(77.2200 28.6400)', 4326), 275.00, '7890', 'CASH', 'COMPLETED', '2026-04-03 08:00:00', '2026-04-03 08:15:00', '2026-04-03 08:50:00'),
(6, 6, 8, ST_GeomFromText('POINT(77.2700 28.6700)', 4326), ST_GeomFromText('POINT(77.2400 28.6100)', 4326), 210.50, '1234', 'WALLET', 'COMPLETED', '2026-04-03 16:00:00', '2026-04-03 16:20:00', '2026-04-03 17:00:00'),
(7, 7, 9, ST_GeomFromText('POINT(77.2300 28.6600)', 4326), ST_GeomFromText('POINT(77.1900 28.6000)', 4326), 165.75, '5678', 'WALLET', 'COMPLETED', '2026-04-03 18:30:00', '2026-04-03 18:45:00', '2026-04-03 19:15:00'),
(8, 8, 10, ST_GeomFromText('POINT(77.1800 28.6300)', 4326), ST_GeomFromText('POINT(77.1500 28.6500)', 4326), 145.00, '9012', 'CASH', 'COMPLETED', '2026-04-04 07:00:00', '2026-04-04 07:10:00', '2026-04-04 07:35:00'),
(9, 9, 11, ST_GeomFromText('POINT(77.1200 28.6700)', 4326), ST_GeomFromText('POINT(77.1700 28.6800)', 4326), 285.25, '3456', 'WALLET', 'COMPLETED', '2026-04-04 12:00:00', '2026-04-04 12:15:00', '2026-04-04 12:55:00'),
(10, 10, 12, ST_GeomFromText('POINT(77.1400 28.6900)', 4326), ST_GeomFromText('POINT(77.1300 28.7000)', 4326), 125.50, '7890', 'WALLET', 'COMPLETED', '2026-04-04 15:30:00', '2026-04-04 15:40:00', '2026-04-04 16:00:00');

-- CANCELLED rides
INSERT INTO ride (id, rider_id, driver_id, pickup_location, drop_off_location, fare, otp, payment_method, ride_status, created_time) VALUES
(11, 11, 2, ST_GeomFromText('POINT(77.1025 28.7041)', 4326), ST_GeomFromText('POINT(77.2167 28.6667)', 4326), NULL, '1234', 'WALLET', 'CANCELLED', '2026-04-04 19:00:00'),
(12, 12, 3, ST_GeomFromText('POINT(77.2167 28.6667)', 4326), ST_GeomFromText('POINT(77.2273 28.6353)', 4326), NULL, '5678', 'CASH', 'CANCELLED', '2026-04-03 20:00:00'),
(13, 13, 4, ST_GeomFromText('POINT(77.2273 28.6353)', 4326), ST_GeomFromText('POINT(77.2500 28.5500)', 4326), NULL, '9012', 'WALLET', 'CANCELLED', '2026-04-02 21:00:00');

-- ONGOING/ACCEPTED rides
INSERT INTO ride (id, rider_id, driver_id, pickup_location, drop_off_location, fare, otp, payment_method, ride_status, created_time, started_at) VALUES
(14, 14, 15, ST_GeomFromText('POINT(77.1500 28.6500)', 4326), ST_GeomFromText('POINT(77.1200 28.6700)', 4326), NULL, '3456', 'WALLET', 'ACCEPTED', '2026-04-04 20:00:00', '2026-04-04 20:10:00'),
(15, 15, 16, ST_GeomFromText('POINT(77.1200 28.6700)', 4326), ST_GeomFromText('POINT(77.1700 28.6800)', 4326), NULL, '7890', 'WALLET', 'ACCEPTED', '2026-04-04 20:30:00', NULL);

-- Payments for completed rides
INSERT INTO payment (id, ride_id, user_id, amount, payment_method, payment_status, payment_time) VALUES
(1, 1, 1, 245.50, 'WALLET', 'COMPLETED', '2026-04-01 10:46:00'),
(2, 2, 3, 180.00, 'CASH', 'COMPLETED', '2026-04-01 12:16:00'),
(3, 3, 4, 320.75, 'WALLET', 'COMPLETED', '2026-04-02 10:01:00'),
(4, 4, 5, 195.25, 'WALLET', 'COMPLETED', '2026-04-02 14:41:00'),
(5, 5, 6, 275.00, 'CASH', 'COMPLETED', '2026-04-03 08:51:00'),
(6, 6, 7, 210.50, 'WALLET', 'COMPLETED', '2026-04-03 17:01:00'),
(7, 7, 8, 165.75, 'WALLET', 'COMPLETED', '2026-04-03 19:16:00'),
(8, 8, 9, 145.00, 'CASH', 'COMPLETED', '2026-04-04 07:36:00'),
(9, 9, 10, 285.25, 'WALLET', 'COMPLETED', '2026-04-04 12:56:00'),
(10, 10, 11, 125.50, 'WALLET', 'COMPLETED', '2026-04-04 16:01:00');

-- Wallet transactions for rides
-- Debits from rider payments
INSERT INTO wallet_transaction (id, wallet_id, ride_id, amount, transaction_type, transaction_method, transaction_id, timestamp) VALUES
(1, 1, 1, 245.50, 'DEBIT', 'RIDE_PAYMENT', 'TXN001', '2026-04-01 10:46:00'),
(3, 3, 2, 180.00, 'DEBIT', 'RIDE_PAYMENT', 'TXN003', '2026-04-01 12:16:00'),
(4, 4, 3, 320.75, 'DEBIT', 'RIDE_PAYMENT', 'TXN004', '2026-04-02 10:01:00'),
(5, 5, 4, 195.25, 'DEBIT', 'RIDE_PAYMENT', 'TXN005', '2026-04-02 14:41:00'),
(6, 7, 6, 210.50, 'DEBIT', 'RIDE_PAYMENT', 'TXN006', '2026-04-03 17:01:00'),
(7, 8, 7, 165.75, 'DEBIT', 'RIDE_PAYMENT', 'TXN007', '2026-04-03 19:16:00'),
(8, 10, 9, 285.25, 'DEBIT', 'RIDE_PAYMENT', 'TXN008', '2026-04-04 12:56:00'),
(9, 11, 10, 125.50, 'DEBIT', 'RIDE_PAYMENT', 'TXN009', '2026-04-04 16:01:00');

-- Credits to driver wallets (earnings)
INSERT INTO wallet_transaction (id, wallet_id, ride_id, amount, transaction_type, transaction_method, transaction_id, timestamp) VALUES
(10, 2, 1, 220.95, 'CREDIT', 'RIDE_EARNING', 'TXN010', '2026-04-01 10:46:00'),
(11, 3, 2, 162.00, 'CREDIT', 'RIDE_EARNING', 'TXN011', '2026-04-01 12:16:00'),
(12, 5, 3, 288.68, 'CREDIT', 'RIDE_EARNING', 'TXN012', '2026-04-02 10:01:00'),
(13, 6, 4, 175.73, 'CREDIT', 'RIDE_EARNING', 'TXN013', '2026-04-02 14:41:00'),
(14, 8, 6, 189.45, 'CREDIT', 'RIDE_EARNING', 'TXN014', '2026-04-03 17:01:00'),
(15, 9, 7, 149.18, 'CREDIT', 'RIDE_EARNING', 'TXN015', '2026-04-03 19:16:00'),
(16, 12, 9, 256.73, 'CREDIT', 'RIDE_EARNING', 'TXN016', '2026-04-04 12:56:00'),
(17, 13, 10, 112.95, 'CREDIT', 'RIDE_EARNING', 'TXN017', '2026-04-04 16:01:00');

-- Wallet top-ups
INSERT INTO wallet_transaction (id, wallet_id, amount, transaction_type, transaction_method, transaction_id, timestamp) VALUES
(18, 1, 500.00, 'CREDIT', 'TOP_UP', 'TXN018', '2026-03-15 10:00:00'),
(19, 2, 1000.00, 'CREDIT', 'TOP_UP', 'TXN019', '2026-03-10 15:00:00'),
(20, 3, 300.00, 'CREDIT', 'TOP_UP', 'TXN020', '2026-03-20 12:00:00');