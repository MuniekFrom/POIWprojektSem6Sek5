package com.clinic.controller;

import com.clinic.dto.DoctorRegisterRequest;
import com.clinic.dto.LoginRequest;
import com.clinic.dto.LoginResponse;
import com.clinic.dto.PatientRegisterRequest;
import com.clinic.exception.BusinessValidationException;
import com.clinic.model.Doctor;
import com.clinic.model.Patient;
import com.clinic.model.User;
import com.clinic.model.enums.Role;
import com.clinic.model.enums.UserStatus;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.PatientRepository;
import com.clinic.repository.UserRepository;
import com.clinic.security.CustomUserDetailsService;
import com.clinic.security.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          JwtService jwtService,
                          UserRepository userRepository,
                          PatientRepository patientRepository,
                          DoctorRepository doctorRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessValidationException("Account is waiting for admin approval");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        String role = user.getRole().name();

        return ResponseEntity.ok(new LoginResponse(jwtToken, role));
    }

    @Transactional
    @PostMapping("/register/patient")
    public ResponseEntity<String> registerPatient(@Valid @RequestBody PatientRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessValidationException("Email is already used");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.PATIENT);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setPesel(request.getPesel());
        patient.setPhone(request.getPhone());

        patientRepository.save(patient);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Patient account created successfully");
    }

    @Transactional
    @PostMapping("/register/doctor")
    public ResponseEntity<String> registerDoctor(@Valid @RequestBody DoctorRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessValidationException("Email is already used");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.DOCTOR);
        user.setStatus(UserStatus.PENDING);

        User savedUser = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSpecialization(request.getSpecialization());

        doctorRepository.save(doctor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Doctor registration request created. Waiting for admin approval.");
    }
}