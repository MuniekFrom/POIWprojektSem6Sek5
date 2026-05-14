package com.clinic.dto;

import java.time.LocalDateTime;

public class AppointmentSlotResponse {

    private Long id;
    private String doctorName;
    private String specialization;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long patientId;
    private String patientName;

    public AppointmentSlotResponse() {
    }

    public AppointmentSlotResponse(Long id, String doctorName, String specialization,
                                   LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.id = id;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public AppointmentSlotResponse(Long id, String doctorName, String specialization,
                                   LocalDateTime startTime, LocalDateTime endTime, String status,
                                   Long patientId, String patientName) {
        this.id = id;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.patientId = patientId;
        this.patientName = patientName;
    }

    public Long getId() {
        return id;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}