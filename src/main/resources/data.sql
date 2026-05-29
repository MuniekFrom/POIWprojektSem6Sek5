INSERT IGNORE INTO users (id, email, password, role, status)
VALUES
(1, 'admin@example.com', '$2a$10$oPQurQBq3Lffvpb7CzeX9uSvy6Gxzz5j5hGCP.gA39icmX.aFEQpC', 'ADMIN', 'ACTIVE'),

(2, 'doctor@example.com', '$2a$10$oPQurQBq3Lffvpb7CzeX9uSvy6Gxzz5j5hGCP.gA39icmX.aFEQpC', 'DOCTOR', 'ACTIVE'),
(3, 'doctor2@example.com', '$2a$10$oPQurQBq3Lffvpb7CzeX9uSvy6Gxzz5j5hGCP.gA39icmX.aFEQpC', 'DOCTOR', 'ACTIVE'),

(4, 'anna@example.com', '$2a$10$oPQurQBq3Lffvpb7CzeX9uSvy6Gxzz5j5hGCP.gA39icmX.aFEQpC', 'PATIENT', 'ACTIVE'),
(5, 'marek@example.com', '$2a$10$oPQurQBq3Lffvpb7CzeX9uSvy6Gxzz5j5hGCP.gA39icmX.aFEQpC', 'PATIENT', 'ACTIVE');


INSERT IGNORE INTO doctors (id, user_id, first_name, last_name, specialization)
VALUES
(1, 2, 'Jan', 'Kowalski', 'Kardiolog'),
(2, 3, 'Piotr', 'Zieliński', 'Dermatolog');


INSERT IGNORE INTO patients (id, user_id, pesel, first_name, last_name, phone)
VALUES
(1, 4, '12345678901', 'Anna', 'Nowak', '123456789'),
(2, 5, '98765432109', 'Marek', 'Wiśniewski', '987654321');


INSERT IGNORE INTO appointment_slot (id, doctor_id, start_time, end_time, status)
VALUES
(1, 1, '2026-12-01 09:00:00', '2026-12-01 09:30:00', 'AVAILABLE'),
(2, 1, '2026-12-01 10:00:00', '2026-12-01 10:30:00', 'AVAILABLE'),
(3, 1, '2026-12-02 11:00:00', '2026-12-02 11:30:00', 'BOOKED'),
(4, 1, '2026-12-02 12:00:00', '2026-12-02 12:30:00', 'AVAILABLE'),

(5, 2, '2026-12-03 09:00:00', '2026-12-03 09:30:00', 'AVAILABLE'),
(6, 2, '2026-12-03 10:00:00', '2026-12-03 10:30:00', 'BOOKED'),
(7, 2, '2026-12-04 13:00:00', '2026-12-04 13:30:00', 'AVAILABLE'),
(8, 2, '2026-12-04 14:00:00', '2026-12-04 14:30:00', 'AVAILABLE');


INSERT IGNORE INTO appointment (id, patient_id, appointment_slot_id, booked_at, reason, status)
VALUES
(1, 1, 3, '2026-05-29 10:00:00', 'Kontrola kardiologiczna', 'BOOKED'),
(2, 2, 6, '2026-05-29 10:15:00', 'Konsultacja dermatologiczna', 'BOOKED');