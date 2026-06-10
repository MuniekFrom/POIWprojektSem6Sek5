package com.example.przychodnia_mobile;

public class AppointmentRequest {
    private Long slotId;
    private String reason;

    public AppointmentRequest(Long slotId, String reason) {
        this.slotId = slotId;
        this.reason = reason;
    }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}