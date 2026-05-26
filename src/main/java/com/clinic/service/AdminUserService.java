package com.clinic.service;

import com.clinic.dto.AdminUserResponse;
import com.clinic.exception.BusinessValidationException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.model.Doctor;
import com.clinic.model.Patient;
import com.clinic.model.User;
import com.clinic.model.enums.Role;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.PatientRepository;
import com.clinic.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AdminUserService(UserRepository userRepository,
                            DoctorRepository doctorRepository,
                            PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToAdminUserResponse)
                .collect(Collectors.toList());
    }

    public void deleteUserByAdmin(Long userId, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (admin.getId().equals(user.getId())) {
            throw new BusinessValidationException("Admin cannot delete own account");
        }

        if (user.getRole() == Role.DOCTOR) {
            Doctor doctor = doctorRepository.findByUserId(userId)
                    .orElse(null);

            if (doctor != null) {
                doctorRepository.delete(doctor);
            }
        }

        if (user.getRole() == Role.PATIENT) {
            Patient patient = patientRepository.findByUserId(userId)
                    .orElse(null);

            if (patient != null) {
                patientRepository.delete(patient);
            }
        }

        userRepository.delete(user);
    }

    private AdminUserResponse mapToAdminUserResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name()
        );
    }
}