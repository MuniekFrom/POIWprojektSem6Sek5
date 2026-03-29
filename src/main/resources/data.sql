INSERT INTO users (id, email, password, role)
VALUES (1, 'doctor@example.com', 'test', 'DOCTOR');

INSERT INTO users (id, email, password, role)
VALUES (2, 'anna@example.com', 'test', 'PATIENT');

INSERT INTO doctors (id, user_id, first_name, last_name, specialization)
VALUES (1, 1, 'Jan', 'Kowalski', 'Kardiolog');

INSERT INTO patients (id, user_id, pesel, first_name, last_name, phone)
VALUES (1, 2, '12345678901', 'Anna', 'Nowak', '123456789');

INSERT INTO appointment_slot (id, doctor_id, start_time, end_time, status)
VALUES (1, 1, '2026-04-01 10:00:00', '2026-04-01 10:30:00', 'AVAILABLE');

INSERT INTO appointment_slot (id, doctor_id, start_time, end_time, status)
VALUES (2, 1, '2026-04-01 11:00:00', '2026-04-01 11:30:00', 'AVAILABLE');