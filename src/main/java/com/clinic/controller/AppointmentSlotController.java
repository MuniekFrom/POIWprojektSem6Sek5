package com.clinic.controller;

import com.clinic.dto.AppointmentSlotResponse;
import com.clinic.dto.CreateAppointmentSlotRequest;
import com.clinic.service.AppointmentSlotService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/slots")
public class AppointmentSlotController {

    private final AppointmentSlotService appointmentSlotService;

    public AppointmentSlotController(AppointmentSlotService appointmentSlotService) {
        this.appointmentSlotService = appointmentSlotService;
    }

    @PostMapping
    public AppointmentSlotResponse createSlot(@RequestBody CreateAppointmentSlotRequest request) {
        return appointmentSlotService.createSlot(request);
    }
}