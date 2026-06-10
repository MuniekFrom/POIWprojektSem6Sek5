package com.example.przychodnia_mobile;

public class SlotResponse {
    private Long id;
    private String startTime;
    private String endTime;
    private boolean available;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}