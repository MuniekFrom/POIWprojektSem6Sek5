package com.clinic.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AppointmentSlot {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "docor_id")
    private Doctor doctor;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private AppointmentSlotStatus status;

    public AppointmentSlot(){
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

    public void setId(Long id) {
        this.id = id;
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


    public void book(){
        this.status = AppointmentSlotStatus.BOOKED;
    }


}
