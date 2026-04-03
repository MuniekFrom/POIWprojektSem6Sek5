package com.clinic.service;

import com.clinic.dto.AppointmentSlotResponse;
import com.clinic.dto.CreateAppointmentSlotRequest;
import com.clinic.exception.BusinessValidationException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.model.AppointmentSlot;
import com.clinic.model.AppointmentSlotStatus;
import com.clinic.model.Doctor;
import com.clinic.repository.AppointmentSlotRepository;
import com.clinic.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentSlotService {

    private final AppointmentSlotRepository appointmentSlotRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentSlotService(AppointmentSlotRepository appointmentSlotRepository,
                                  DoctorRepository doctorRepository) {
        this.appointmentSlotRepository = appointmentSlotRepository;
        this.doctorRepository = doctorRepository;
    }

    public AppointmentSlotResponse createSlot(CreateAppointmentSlotRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + request.getDoctorId()
                ));

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("startTime and endTime cannot be null");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessValidationException("endTime must be after startTime");
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessValidationException("Cannot create slot in the past");
        }

        boolean overlaps = appointmentSlotRepository
                .existsByDoctorAndStartTimeLessThanAndEndTimeGreaterThan(
                        doctor,
                        request.getEndTime(),
                        request.getStartTime()
                );

        if (overlaps) {
            throw new BusinessValidationException("Slot overlaps with existing doctor's slot");
        }

        AppointmentSlot slot = new AppointmentSlot();
        slot.setDoctor(doctor);
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setStatus(AppointmentSlotStatus.AVAILABLE);

        AppointmentSlot savedSlot = appointmentSlotRepository.save(slot);

        return new AppointmentSlotResponse(
                savedSlot.getId(),
                savedSlot.getDoctor().getFirstName() + " " + savedSlot.getDoctor().getLastName(),
                savedSlot.getDoctor().getSpecialization(),
                savedSlot.getStartTime(),
                savedSlot.getEndTime(),
                savedSlot.getStatus().name()
        );
    }
}