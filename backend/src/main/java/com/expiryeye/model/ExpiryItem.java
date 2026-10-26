package com.expiryeye.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
//LOGIN SIGNUP UPDATION!!
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "items")
public class ExpiryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Updation of SIGNUP AND LOGIN FORM!!
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private Integer reminderDays;

    private String ownerName;
    
    private Integer quantity; // <--- Yeh quantity field add kar di hai

    private Double estimatedValue;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public ExpiryItem() {}

    public ExpiryItem(String name, ItemCategory category, LocalDate expiryDate, Integer reminderDays, String ownerName, Integer quantity, Double estimatedValue, String notes) {
        this.name = name;
        this.category = category;
        this.expiryDate = expiryDate;
        this.reminderDays = reminderDays;
        this.ownerName = ownerName;
        this.quantity = quantity;
        this.estimatedValue = estimatedValue;
        this.notes = notes;
    }

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

    // --- QUANTITY GETTERS & SETTERS ---
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(Double estimatedValue) { this.estimatedValue = estimatedValue; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    //UPDATION OF SIGNUP AND LOGIN FORM!!
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}