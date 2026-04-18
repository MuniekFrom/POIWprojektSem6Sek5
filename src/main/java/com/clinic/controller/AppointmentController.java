package com.clinic.controller;

import com.clinic.dto.AppointmentRequest;
import com.clinic.dto.AppointmentResponse;
import com.clinic.dto.AppointmentSlotResponse;
import com.clinic.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<AppointmentSlotResponse>> getAvailableSlots(@RequestParam Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(doctorId));
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(appointmentService.bookAppointment(request, email));
    }


    @GetMapping("/me")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(appointmentService.getAppointmentsForLoggedPatient(email));
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        appointmentService.cancelAppointment(appointmentId, email);
        return ResponseEntity.noContent().build();
    }
}