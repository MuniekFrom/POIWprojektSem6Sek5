package com.clinic.repository;

import com.clinic.model.AppointmentSlot;
import com.clinic.model.enums.AppointmentSlotStatus;
import com.clinic.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    List<AppointmentSlot> findByDoctorIdAndStatus(Long doctorId, AppointmentSlotStatus status);

    List<AppointmentSlot> findByDoctorId(Long doctorId);

    boolean existsByDoctorAndStartTimeLessThanAndEndTimeGreaterThan(
            Doctor doctor,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}