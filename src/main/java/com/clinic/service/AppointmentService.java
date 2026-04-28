package com.clinic.service;

import com.clinic.dto.AppointmentRequest;
import com.clinic.dto.AppointmentResponse;
import com.clinic.dto.AppointmentSlotResponse;
import com.clinic.exception.BusinessValidationException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.model.Appointment;
import com.clinic.model.AppointmentSlot;
import com.clinic.model.AppointmentSlotStatus;
import com.clinic.model.Patient;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.AppointmentSlotRepository;
import com.clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              AppointmentSlotRepository appointmentSlotRepository,
                              PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentSlotRepository = appointmentSlotRepository;
        this.patientRepository = patientRepository;
    }

    public AppointmentResponse bookAppointment(AppointmentRequest request, String email) {
        AppointmentSlot slot = appointmentSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Slot not found with id: " + request.getSlotId()
                ));

        if (!slot.isAvailable()) {
            throw new BusinessValidationException("Slot is already booked");
        }

        if (appointmentRepository.existsByAppointmentSlotId(slot.getId())) {
            throw new BusinessValidationException("Appointment already exists for this slot");
        }

        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found for email: " + email
                ));

        Appointment appointment = new Appointment();
        appointment.setAppointmentSlot(slot);
        appointment.setPatient(patient);
        appointment.setBookedAt(LocalDateTime.now());
        appointment.setReason(request.getReason());

        slot.setStatus(AppointmentSlotStatus.BOOKED);
        appointmentSlotRepository.save(slot);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return mapToAppointmentResponse(savedAppointment);
    }


    public List<AppointmentResponse> getAppointmentsForLoggedPatient(String email) {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found for email: " + email
                ));

        return appointmentRepository.findByPatientId(patient.getId())
                .stream()
                .filter(appointment -> appointment.getAppointmentSlot().getStatus() == AppointmentSlotStatus.BOOKED)
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentSlotResponse> getAvailableSlots(Long doctorId) {
        return appointmentSlotRepository.findByDoctorIdAndStatus(doctorId, AppointmentSlotStatus.AVAILABLE)
                .stream()
                .map(slot -> new AppointmentSlotResponse(
                        slot.getId(),
                        slot.getDoctor().getFirstName() + " " + slot.getDoctor().getLastName(),
                        slot.getDoctor().getSpecialization(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        slot.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    public void cancelAppointment(Long appointmentId, String email) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + appointmentId
                ));

        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found for email: " + email
                ));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new BusinessValidationException("You can cancel only your own appointments");
        }

        AppointmentSlot slot = appointment.getAppointmentSlot();
        slot.setStatus(AppointmentSlotStatus.AVAILABLE);

        appointmentSlotRepository.save(slot);

        // NIE USUWAJ NA RAZIE:
        // appointmentRepository.delete(appointment);
    }

    private AppointmentResponse mapToAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getAppointmentSlot().getDoctor().getFirstName() + " " +
                        appointment.getAppointmentSlot().getDoctor().getLastName(),
                appointment.getAppointmentSlot().getDoctor().getSpecialization(),
                appointment.getPatient().getFirstName() + " " +
                        appointment.getPatient().getLastName(),
                appointment.getAppointmentSlot().getStartTime(),
                appointment.getAppointmentSlot().getEndTime(),
                appointment.getBookedAt(),
                appointment.getReason(),
                appointment.getAppointmentSlot().getStatus().name()
        );
    }
}