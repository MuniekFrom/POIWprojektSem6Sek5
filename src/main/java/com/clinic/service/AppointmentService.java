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

    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        AppointmentSlot slot = appointmentSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Slot not found with id: " + request.getSlotId()
                ));

        if (!slot.isAvailable()) {
            throw new BusinessValidationException("Slot is already booked");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + request.getPatientId()
                ));

        Appointment appointment = new Appointment();
        appointment.setAppointmentSlot(slot);
        appointment.setPatient(patient);
        appointment.setBookedAt(LocalDateTime.now());
        appointment.setReason(request.getReason());

        slot.setStatus(AppointmentSlotStatus.BOOKED);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        appointmentSlotRepository.save(slot);

        return new AppointmentResponse(
                savedAppointment.getId(),
                savedAppointment.getAppointmentSlot().getDoctor().getFirstName() + " " +
                        savedAppointment.getAppointmentSlot().getDoctor().getLastName(),
                savedAppointment.getAppointmentSlot().getDoctor().getSpecialization(),
                savedAppointment.getPatient().getFirstName() + " " +
                        savedAppointment.getPatient().getLastName(),
                savedAppointment.getAppointmentSlot().getStartTime(),
                savedAppointment.getAppointmentSlot().getEndTime(),
                savedAppointment.getBookedAt(),
                savedAppointment.getReason(),
                savedAppointment.getAppointmentSlot().getStatus().name()
        );
    }

    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(appointment -> new AppointmentResponse(
                        appointment.getId(),
                        appointment.getAppointmentSlot().getDoctor().getFirstName() + " " +
                                appointment.getAppointmentSlot().getDoctor().getLastName(),
                        appointment.getAppointmentSlot().getDoctor().getSpecialization(),
                        appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName(),
                        appointment.getAppointmentSlot().getStartTime(),
                        appointment.getAppointmentSlot().getEndTime(),
                        appointment.getBookedAt(),
                        appointment.getReason(),
                        appointment.getAppointmentSlot().getStatus().name()
                ))
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

    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + appointmentId
                ));

        AppointmentSlot slot = appointment.getAppointmentSlot();
        slot.setStatus(AppointmentSlotStatus.AVAILABLE);

        appointmentRepository.delete(appointment);
        appointmentSlotRepository.save(slot);
    }
}