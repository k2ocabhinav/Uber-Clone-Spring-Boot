-- Seed data aligned with the current JPA schema.
-- Shared bcrypt hash for password: password123

INSERT INTO app_user (id, first_name, last_name, email, password, phone_number, active, created_time) VALUES
(1, 'Alice', 'Rider', 'alice@example.com', '$2y$10$Nt93cyP1zWSTQC12nvIGkO6OuFhozbuu48Kjv6Z0UMRtATnhYFy0a', '+91-9000000001', true, '2026-04-01 08:00:00'),
(2, 'Bob', 'Driver', 'bob.driver@example.com', '$2y$10$Nt93cyP1zWSTQC12nvIGkO6OuFhozbuu48Kjv6Z0UMRtATnhYFy0a', '+91-9000000002', true, '2026-04-01 08:05:00'),
(3, 'Carol', 'Captain', 'carol.driver@example.com', '$2y$10$Nt93cyP1zWSTQC12nvIGkO6OuFhozbuu48Kjv6Z0UMRtATnhYFy0a', '+91-9000000003', true, '2026-04-01 08:10:00'),
(4, 'Dave', 'Rider', 'dave@example.com', '$2y$10$Nt93cyP1zWSTQC12nvIGkO6OuFhozbuu48Kjv6Z0UMRtATnhYFy0a', '+91-9000000004', true, '2026-04-02 09:00:00'),
(41, 'Admin', 'User', 'admin@uberclone.com', '$2y$10$Nt93cyP1zWSTQC12nvIGkO6OuFhozbuu48Kjv6Z0UMRtATnhYFy0a', '+91-9999999999', true, '2026-04-01 07:30:00');

INSERT INTO user_roles (user_id, roles) VALUES
(1, 'RIDER'),
(2, 'RIDER'),
(2, 'DRIVER'),
(3, 'RIDER'),
(3, 'DRIVER'),
(4, 'RIDER'),
(41, 'ADMIN');

INSERT INTO rider (id, user_id, rating) VALUES
(1, 1, 4.9),
(2, 2, 4.7),
(3, 3, 4.8),
(4, 4, 4.5);

INSERT INTO driver (id, user_id, vehicle_id, rating, available, current_location, created_at) VALUES
(1, 2, 'KA01AB1234', 4.8, true, ST_GeomFromText('POINT(77.5946 12.9716)', 4326), '2026-04-01 08:15:00'),
(2, 3, 'KA01CD5678', 4.6, true, ST_GeomFromText('POINT(77.6090 12.9800)', 4326), '2026-04-01 08:20:00');

INSERT INTO wallet (id, user_id, balance) VALUES
(1, 1, 850.00),
(2, 2, 500.00),
(3, 3, 420.00),
(4, 4, 275.00),
(5, 41, 0.00);

INSERT INTO ride_request (id, pickup_location, drop_off_location, requested_time, rider_id, payment_method, ride_request_status, fare) VALUES
(1, ST_GeomFromText('POINT(77.6100 12.9350)', 4326), ST_GeomFromText('POINT(77.6200 12.9450)', 4326), '2026-04-05 08:45:00', 4, 'WALLET', 'PENDING', 180.00);

INSERT INTO ride (id, pickup_location, drop_off_location, created_time, started_at, ended_at, rider_id, driver_id, payment_method, ride_status, fare, otp) VALUES
(1, ST_GeomFromText('POINT(77.5946 12.9716)', 4326), ST_GeomFromText('POINT(77.5800 12.9600)', 4326), '2026-04-03 09:00:00', '2026-04-03 09:10:00', '2026-04-03 09:40:00', 1, 1, 'WALLET', 'ENDED', 150.00, '1234'),
(2, ST_GeomFromText('POINT(77.6200 12.9900)', 4326), ST_GeomFromText('POINT(77.6400 12.9950)', 4326), '2026-04-03 18:00:00', null, null, 4, 2, 'CASH', 'CANCELLED', 90.00, '5678'),
(3, ST_GeomFromText('POINT(77.6000 12.9500)', 4326), ST_GeomFromText('POINT(77.6150 12.9650)', 4326), '2026-04-04 20:00:00', '2026-04-04 20:10:00', null, 2, 2, 'CASH', 'ONGOING', 120.00, '2468'),
(4, ST_GeomFromText('POINT(77.6100 12.9350)', 4326), ST_GeomFromText('POINT(77.6250 12.9550)', 4326), '2026-04-05 09:00:00', null, null, 4, 1, 'WALLET', 'CONFIRMED', 200.00, '1357');

INSERT INTO payment (id, payment_method, user_id, ride_id, amount, payment_status, payment_time) VALUES
(1, 'WALLET', 1, 1, 150.00, 'CONFIRMED', '2026-04-03 09:41:00'),
(2, 'CASH', 2, 3, 120.00, 'PENDING', '2026-04-04 20:11:00');

INSERT INTO rating (id, ride_id, rider_id, driver_id, driver_rating, rider_rating) VALUES
(1, 1, 1, 1, 5, 4);

INSERT INTO wallet_transaction (id, amount, transaction_type, transaction_method, transaction_id, ride_id, wallet_id, timestamp) VALUES
(1, 300.00, 0, 2, 'TOPUP-ALICE-001', null, 1, '2026-04-02 08:00:00'),
(2, 150.00, 1, 1, 'RIDE-1-DEBIT', 1, 1, '2026-04-03 09:41:00'),
(3, 105.00, 0, 1, 'RIDE-1-CREDIT', 1, 2, '2026-04-03 09:41:00');

SELECT setval(pg_get_serial_sequence('app_user', 'id'), (SELECT MAX(id) FROM app_user));
SELECT setval(pg_get_serial_sequence('rider', 'id'), (SELECT MAX(id) FROM rider));
SELECT setval(pg_get_serial_sequence('driver', 'id'), (SELECT MAX(id) FROM driver));
SELECT setval(pg_get_serial_sequence('wallet', 'id'), (SELECT MAX(id) FROM wallet));
SELECT setval(pg_get_serial_sequence('ride_request', 'id'), (SELECT MAX(id) FROM ride_request));
SELECT setval(pg_get_serial_sequence('ride', 'id'), (SELECT MAX(id) FROM ride));
SELECT setval(pg_get_serial_sequence('payment', 'id'), (SELECT MAX(id) FROM payment));
SELECT setval(pg_get_serial_sequence('rating', 'id'), (SELECT MAX(id) FROM rating));
SELECT setval(pg_get_serial_sequence('wallet_transaction', 'id'), (SELECT MAX(id) FROM wallet_transaction));
