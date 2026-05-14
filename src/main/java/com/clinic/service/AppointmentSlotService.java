package com.clinic.service;

import com.clinic.dto.AppointmentSlotResponse;
import com.clinic.dto.CreateAppointmentSlotRequest;
import com.clinic.exception.BusinessValidationException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.model.Appointment;
import com.clinic.model.AppointmentSlot;
import com.clinic.model.Doctor;
import com.clinic.model.enums.AppointmentSlotStatus;
import com.clinic.model.enums.AppointmentStatus;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.AppointmentSlotRepository;
import com.clinic.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentSlotService {

    private final AppointmentSlotRepository appointmentSlotRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public AppointmentSlotService(AppointmentSlotRepository appointmentSlotRepository,
                                  DoctorRepository doctorRepository,
                                  AppointmentRepository appointmentRepository) {
        this.appointmentSlotRepository = appointmentSlotRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public AppointmentSlotResponse createSlot(CreateAppointmentSlotRequest request, String email) {
        Doctor doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found for email: " + email
                ));

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("startTime and endTime cannot be null");
        }

        LocalDateTime now = LocalDateTime.now();

        if (request.getStartTime().isBefore(now)) {
            throw new BusinessValidationException("Cannot create slot in the past");
        }

        if (request.getEndTime().isBefore(now)) {
            throw new BusinessValidationException("Cannot create slot that has already ended");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessValidationException("endTime must be after startTime");
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
        updatePastSlots();

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

        if (slot.getStatus() == AppointmentSlotStatus.COMPLETED) {
            throw new BusinessValidationException("Cannot delete completed slot");
        }

        appointmentSlotRepository.delete(slot);
    }

    public List<AppointmentSlotResponse> getSlotsForLoggedDoctor(String email) {
        updatePastSlots();

        Doctor doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found for email: " + email
                ));

        return appointmentSlotRepository.findByDoctorId(doctor.getId())
                .stream()
                .map(this::mapToAppointmentSlotResponse)
                .collect(Collectors.toList());
    }

    private void updatePastSlots() {
        List<AppointmentSlot> oldSlots = appointmentSlotRepository.findByEndTimeBeforeAndStatusIn(
                LocalDateTime.now(),
                List.of(AppointmentSlotStatus.AVAILABLE, AppointmentSlotStatus.BOOKED)
        );

        if (oldSlots.isEmpty()) {
            return;
        }

        for (AppointmentSlot slot : oldSlots) {
            slot.setStatus(AppointmentSlotStatus.COMPLETED);
        }

        appointmentSlotRepository.saveAll(oldSlots);
    }

    private AppointmentSlotResponse mapToAppointmentSlotResponse(AppointmentSlot slot) {
        Long patientId = null;
        String patientName = null;

        if (slot.getStatus() == AppointmentSlotStatus.BOOKED ||
                slot.getStatus() == AppointmentSlotStatus.COMPLETED) {

            Optional<Appointment> appointmentOptional =
                    appointmentRepository.findFirstByAppointmentSlotIdAndStatusIn(
                            slot.getId(),
                            List.of(AppointmentStatus.BOOKED, AppointmentStatus.COMPLETED)
                    );

            if (appointmentOptional.isPresent()) {
                Appointment appointment = appointmentOptional.get();

                patientId = appointment.getPatient().getId();
                patientName = appointment.getPatient().getFirstName() + " " +
                        appointment.getPatient().getLastName();
            }
        }

        return new AppointmentSlotResponse(
                slot.getId(),
                slot.getDoctor().getFirstName() + " " + slot.getDoctor().getLastName(),
                slot.getDoctor().getSpecialization(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus().name(),
                patientId,
                patientName
        );
    }
}