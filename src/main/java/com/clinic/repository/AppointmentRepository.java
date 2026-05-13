package com.clinic.repository;

import com.clinic.model.Appointment;
import com.clinic.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    boolean existsByAppointmentSlotIdAndStatus(Long slotId, AppointmentStatus status);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByAppointmentSlotDoctorUserEmailAndAppointmentSlotStartTimeBetweenAndStatusIn(
            String email,
            LocalDateTime start,
            LocalDateTime end,
            List<AppointmentStatus> statuses
    );
}