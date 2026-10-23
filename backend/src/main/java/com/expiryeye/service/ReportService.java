package com.expiryeye.service;

import com.expiryeye.dto.DashboardResponse;
import com.expiryeye.dto.ItemResponse;
import com.expiryeye.model.ItemStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ItemService itemService;

    public ReportService(ItemService itemService) {
        this.itemService = itemService;
    }

    public DashboardResponse getDashboardData() {
        List<ItemResponse> items = itemService.getAllItems();

        long total = items.size();
        long expiringSoon = items.stream().filter(i -> i.getStatus() == ItemStatus.EXPIRING_SOON).count();
        long expired = items.stream().filter(i -> i.getStatus() == ItemStatus.EXPIRED).count();
        long reminderDue = items.stream().filter(i -> i.getStatus() == ItemStatus.REMINDER_DUE).count();

        double valueAtRisk = items.stream()
                .filter(i -> i.getStatus() == ItemStatus.EXPIRED || i.getStatus() == ItemStatus.EXPIRING_SOON)
                .mapToDouble(i -> i.getEstimatedValue() != null ? i.getEstimatedValue() : 0.0)
                .sum();

        Map<String, Long> categoryCount = items.stream()
                .collect(Collectors.groupingBy(i -> i.getCategory().name(), Collectors.counting()));

        DashboardResponse resp = new DashboardResponse();
        resp.setTotalItems(total);
        resp.setExpiringSoonItems(expiringSoon);
        resp.setExpiredItems(expired);
        resp.setReminderDueItems(reminderDue);
        resp.setValueAtRisk(valueAtRisk);
        resp.setCategoryWiseCount(categoryCount);

        return resp;
    }

    public Map<String, Object> getWasteReport() {
        List<ItemResponse> items = itemService.getAllItems();

        long totalExpired = items.stream().filter(i -> i.getStatus() == ItemStatus.EXPIRED).count();
        long totalActive = items.stream().filter(i -> i.getStatus() == ItemStatus.ACTIVE).count();
        long withReminders = items.stream().filter(i -> i.getReminderDays() != null && i.getReminderDays() > 0).count();

        double valueAtRisk = items.stream()
                .filter(i -> i.getStatus() == ItemStatus.EXPIRED)
                .mapToDouble(i -> i.getEstimatedValue() != null ? i.getEstimatedValue() : 0.0)
                .sum();

        Map<String, Object> report = new HashMap<>();
        report.put("totalExpiredItems", totalExpired);
        report.put("totalActiveItems", totalActive);
        report.put("itemsWithReminders", withReminders);
        report.put("estimatedMoneyWasted", valueAtRisk);
        report.put("insightMessage", "Tracking items with ExpiryEye helps prevent money loss and medical safety risks.");

        return report;
    }
}
