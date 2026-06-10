package com.example.przychodnia_mobile;

public class CalendarDayResponse {
    private String date; // YYYY-MM-DD
    private String dayName;
    private int slotCount;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDayName() { return dayName; }
    public void setDayName(String dayName) { this.dayName = dayName; }
    public int getSlotCount() { return slotCount; }
    public void setSlotCount(int slotCount) { this.slotCount = slotCount; }
}