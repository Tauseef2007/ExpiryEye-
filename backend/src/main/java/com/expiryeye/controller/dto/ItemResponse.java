package com.expiryeye.dto;

import com.expiryeye.model.ItemCategory;
import com.expiryeye.model.ItemStatus;
import java.time.LocalDate;

public class ItemResponse {
    private Long id;
    private String name;
    private ItemCategory category;
    private LocalDate expiryDate;
    private Integer reminderDays;
    private String ownerName;
    private Double estimatedValue;
    private String notes;
    private long daysRemaining;
    private ItemStatus status;
    private String safetyWarning;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ItemCategory getCategory() { return category; }
    public void setCategory(ItemCategory category) { this.category = category; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Integer getReminderDays() { return reminderDays; }
    public void setReminderDays(Integer reminderDays) { this.reminderDays = reminderDays; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public Double getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(Double estimatedValue) { this.estimatedValue = estimatedValue; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }

    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }

    public String getSafetyWarning() { return safetyWarning; }
    public void setSafetyWarning(String safetyWarning) { this.safetyWarning = safetyWarning; }
}
