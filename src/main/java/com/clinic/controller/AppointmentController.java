package com.clinic.controller;

import com.clinic.dto.AppointmentRequest;
import com.clinic.dto.AppointmentResponse;
import com.clinic.model.Appointment;
import com.clinic.model.AppointmentSlot;
import com.clinic.service.AppointmentService;
import org.springframework.web.bind.annotation.*;
import com.clinic.dto.AppointmentSlotResponse;
import java.util.List;


@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    public Appointment bookAppointment(@RequestBody AppointmentRequest request) {

        return appointmentService.bookAppointment(
                request.getSlotId(),
                request.getPatientId(),
                request.getReason()
        );
    }

    @GetMapping("/available")
    public List<AppointmentSlotResponse> getAvailableSlots(@RequestParam Long doctorId) {
        return appointmentService.getAvailableSlots(doctorId);
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentResponse> getAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @DeleteMapping("/{appointmentId}")
    public String cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
        return "Appointment cancelled";
    }

}