package com.clinic.service;

import com.clinic.dto.PatientProfileResponse;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.model.Patient;
import com.clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientProfileResponse getLoggedPatient(String email) {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found for email: " + email
                ));

        return new PatientProfileResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getPesel(),
                patient.getPhone()
        );
    }
}