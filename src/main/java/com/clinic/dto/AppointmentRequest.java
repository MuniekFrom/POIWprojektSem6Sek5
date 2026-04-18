package com.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AppointmentRequest {

    @NotNull(message = "Slot id is required")
    private Long slotId;

    @NotBlank(message = "Reason is required")
    private String reason;

    public AppointmentRequest() {
    }

    public AppointmentRequest(Long slotId, String reason) {
        this.slotId = slotId;
        this.reason = reason;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}