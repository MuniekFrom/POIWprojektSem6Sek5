package com.clinic.service;

import com.clinic.dto.AppointmentSlotResponse;
import com.clinic.dto.CreateAppointmentSlotRequest;
import com.clinic.exception.BusinessValidationException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.model.AppointmentSlot;
import com.clinic.model.enums.AppointmentSlotStatus;
import com.clinic.model.Doctor;
import com.clinic.repository.AppointmentSlotRepository;
import com.clinic.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentSlotService {

    private final AppointmentSlotRepository appointmentSlotRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentSlotService(AppointmentSlotRepository appointmentSlotRepository,
                                  DoctorRepository doctorRepository) {
        this.appointmentSlotRepository = appointmentSlotRepository;
        this.doctorRepository = doctorRepository;
    }

    public AppointmentSlotResponse createSlot(CreateAppointmentSlotRequest request, String email) {
        Doctor doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found for email: " + email
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

        return mapToAppointmentSlotResponse(savedSlot);
    }

    public void deleteSlot(Long slotId, String email) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Slot not found with id: " + slotId
                ));

        Doctor doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found for email: " + email
                ));

        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new BusinessValidationException("You can delete only your own slots");
        }

        if (slot.getStatus() == AppointmentSlotStatus.BOOKED) {
            throw new BusinessValidationException("Cannot delete booked slot");
        }

        appointmentSlotRepository.delete(slot);
    }

    public List<AppointmentSlotResponse> getSlotsForLoggedDoctor(String email) {
        Doctor doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found for email: " + email
                ));

        return appointmentSlotRepository.findByDoctorId(doctor.getId())
                .stream()
                .map(this::mapToAppointmentSlotResponse)
                .collect(Collectors.toList());
    }

    private AppointmentSlotResponse mapToAppointmentSlotResponse(AppointmentSlot slot) {
        return new AppointmentSlotResponse(
                slot.getId(),
                slot.getDoctor().getFirstName() + " " + slot.getDoctor().getLastName(),
                slot.getDoctor().getSpecialization(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus().name()
        );
    }
}