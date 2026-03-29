package com.clinic.service;

import com.clinic.dto.AppointmentResponse;
import com.clinic.model.Appointment;
import com.clinic.model.AppointmentSlot;
import com.clinic.model.AppointmentSlotStatus;
import com.clinic.model.Patient;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.AppointmentSlotRepository;
import com.clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;
import com.clinic.dto.AppointmentSlotResponse;
import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.List;

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

    public Appointment bookAppointment(Long slotId, Long patientId, String reason) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!slot.isAvailable()) {
            throw new RuntimeException("Slot is already booked");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Appointment appointment = new Appointment();
        appointment.setAppointmentSlot(slot);
        appointment.setPatient(patient);
        appointment.setBookedAt(LocalDateTime.now());
        appointment.setReason(reason);

        slot.book();

        return appointmentRepository.save(appointment);
    }

    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
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
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        AppointmentSlot slot = appointment.getAppointmentSlot();

        slot.setStatus(AppointmentSlotStatus.AVAILABLE);
        slot.setAppointment(null);

        appointmentRepository.delete(appointment);
        appointmentSlotRepository.save(slot);
    }

}