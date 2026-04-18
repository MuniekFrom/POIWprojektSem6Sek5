package com.clinic.controller;

import com.clinic.dto.AppointmentSlotResponse;
import com.clinic.dto.CreateAppointmentSlotRequest;
import com.clinic.service.AppointmentSlotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slots")
public class AppointmentSlotController {

    private final AppointmentSlotService appointmentSlotService;

    public AppointmentSlotController(AppointmentSlotService appointmentSlotService) {
        this.appointmentSlotService = appointmentSlotService;
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteSlot(
            @PathVariable Long slotId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        appointmentSlotService.deleteSlot(slotId, email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<AppointmentSlotResponse> createSlot(
            @Valid @RequestBody CreateAppointmentSlotRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(appointmentSlotService.createSlot(request, email));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AppointmentSlotResponse>> getMySlots(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(appointmentSlotService.getSlotsForLoggedDoctor(email));
    }

}