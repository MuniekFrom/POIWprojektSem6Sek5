package com.clinic.controller;

import com.clinic.dto.AppointmentResponse;
import com.clinic.repository.*;
import com.clinic.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;

    public AdminController(UserRepository userRepository,
                           AppointmentService appointmentService,
                           DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository,
                           AppointmentSlotRepository appointmentSlotRepository) {
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentSlotRepository = appointmentSlotRepository;
    }


    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getAdmin(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "email", authentication.getName(),
                "role", "ADMIN"
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(
                userRepository.findAll()
                        .stream()
                        .map(user -> Map.<String, Object>of(
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "role", user.getRole().name()
                        ))
                        .toList()
        );
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long appointmentId) {
        appointmentService.deleteAppointmentByAdmin(appointmentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId, Authentication authentication){
        appointmentService.deleteUserByAdmin(userId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(Map.of(
                "users", userRepository.count(),
                "doctors", doctorRepository.count(),
                "patients", patientRepository.count(),
                "appointments", appointmentRepository.count(),
                "slots", appointmentSlotRepository.count()
        ));
    }


}