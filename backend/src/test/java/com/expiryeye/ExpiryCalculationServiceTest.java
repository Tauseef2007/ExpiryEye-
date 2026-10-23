package com.expiryeye;

import com.expiryeye.model.ItemCategory;
import com.expiryeye.model.ItemStatus;
import com.expiryeye.service.ExpiryCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ExpiryCalculationServiceTest {

    private ExpiryCalculationService calculationService;
    private final LocalDate today = LocalDate.of(2026, 10, 20);

    @BeforeEach
    void setUp() {
        calculationService = new ExpiryCalculationService();
    }

    @Test
    void testPastExpiryReturnsExpired() {
        LocalDate expiry = today.minusDays(5);
        ItemStatus status = calculationService.calculateStatus(expiry, 7, today);
        assertEquals(ItemStatus.EXPIRED, status);
    }

    @Test
    void testWithin7DaysReturnsExpiringSoon() {
        LocalDate expiry = today.plusDays(4);
        ItemStatus status = calculationService.calculateStatus(expiry, 14, today);
        assertEquals(ItemStatus.EXPIRING_SOON, status);
    }

    @Test
    void testWithinReminderWindowReturnsReminderDue() {
        LocalDate expiry = today.plusDays(10);
        ItemStatus status = calculationService.calculateStatus(expiry, 14, today);
        assertEquals(ItemStatus.REMINDER_DUE, status);
    }

    @Test
    void testFarFutureReturnsActive() {
        LocalDate expiry = today.plusDays(30);
        ItemStatus status = calculationService.calculateStatus(expiry, 7, today);
        assertEquals(ItemStatus.ACTIVE, status);
    }

    @Test
    void testExpiredMedicineGeneratesSafetyWarning() {
        String warning = calculationService.getSafetyWarning(ItemCategory.MEDICINE, ItemStatus.EXPIRED);
        assertNotNull(warning);
        assertTrue(warning.contains("Do not use after expiry"));
    }

    @Test
    void testActiveMedicineNoSafetyWarning() {
        String warning = calculationService.getSafetyWarning(ItemCategory.MEDICINE, ItemStatus.ACTIVE);
        assertNull(warning);
    }
}
