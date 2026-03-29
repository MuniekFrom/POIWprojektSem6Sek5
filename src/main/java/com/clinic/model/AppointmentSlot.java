package com.clinic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class AppointmentSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private AppointmentSlotStatus status;

    @OneToOne(mappedBy = "appointmentSlot")
    @JsonIgnore
    private Appointment appointment;

    public AppointmentSlot() {
    }

    public AppointmentSlot(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime, AppointmentSlotStatus status) {
        this.doctor = doctor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AppointmentSlotStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentSlotStatus status) {
        this.status = status;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void book() {
        this.status = AppointmentSlotStatus.BOOKED;
    }

    public boolean isAvailable() {
        return this.status == AppointmentSlotStatus.AVAILABLE;
    }
}