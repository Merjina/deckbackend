package com.example.deck.dto;
public class ContactDto1 {
    private String preferredDate; // Format: "yyyy-MM-dd"
    private String startTime;     // Format: "HH:mm"
    private String endTime;       // Format: "HH:mm"

    // Getters and Setters
    public String getPreferredDate() { return preferredDate; }
    public void setPreferredDate(String preferredDate) { this.preferredDate = preferredDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
