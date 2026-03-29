package com.clinic.dto;

import java.time.LocalDateTime;

public class AppointmentResponse {

    private Long id;
    private String doctorName;
    private String specialization;
    private String patientName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime bookedAt;
    private String reason;
    private String status;

    public AppointmentResponse() {
    }

    public AppointmentResponse(Long id, String doctorName, String specialization, String patientName,
                               LocalDateTime startTime, LocalDateTime endTime,
                               LocalDateTime bookedAt, String reason, String status) {
        this.id = id;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.patientName = patientName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookedAt = bookedAt;
        this.reason = reason;
        this.status = status;
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

    public String getPatientName() {
        return patientName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
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

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}