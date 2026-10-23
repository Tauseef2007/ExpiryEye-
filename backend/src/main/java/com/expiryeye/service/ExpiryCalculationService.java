package com.expiryeye.service;

import com.expiryeye.model.ItemCategory;
import com.expiryeye.model.ItemStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ExpiryCalculationService {

    public long calculateDaysRemaining(LocalDate expiryDate, LocalDate currentDate) {
        return ChronoUnit.DAYS.between(currentDate, expiryDate);
    }

    public ItemStatus calculateStatus(LocalDate expiryDate, Integer reminderDays, LocalDate currentDate) {
        long daysRemaining = calculateDaysRemaining(expiryDate, currentDate);

        if (daysRemaining < 0) {
            return ItemStatus.EXPIRED;
        } else if (daysRemaining <= 7) {
            return ItemStatus.EXPIRING_SOON;
        } else if (daysRemaining <= reminderDays) {
            return ItemStatus.REMINDER_DUE;
        } else {
            return ItemStatus.ACTIVE;
        }
    }

    public String getSafetyWarning(ItemCategory category, ItemStatus status) {
        if (category == ItemCategory.MEDICINE && status == ItemStatus.EXPIRED) {
            return "Do not use after expiry. Consult a qualified medical professional.";
        }
        return null;
    }
}
