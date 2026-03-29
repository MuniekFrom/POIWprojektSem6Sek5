package com.clinic.repository;

import com.clinic.model.AppointmentSlot;
import com.clinic.model.AppointmentSlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    List<AppointmentSlot> findByDoctorIdAndStatus(Long doctorId, AppointmentSlotStatus status);
}