package com.expiryeye.dto;

import java.util.Map;

public class DashboardResponse {
    private long totalItems;
    private long expiringSoonItems;
    private long expiredItems;
    private long reminderDueItems;
    private double valueAtRisk;
    private Map<String, Long> categoryWiseCount;

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public long getExpiringSoonItems() { return expiringSoonItems; }
    public void setExpiringSoonItems(long expiringSoonItems) { this.expiringSoonItems = expiringSoonItems; }

    public long getExpiredItems() { return expiredItems; }
    public void setExpiredItems(long expiredItems) { this.expiredItems = expiredItems; }

    public long getReminderDueItems() { return reminderDueItems; }
    public void setReminderDueItems(long reminderDueItems) { this.reminderDueItems = reminderDueItems; }

    public double getValueAtRisk() { return valueAtRisk; }
    public void setValueAtRisk(double valueAtRisk) { this.valueAtRisk = valueAtRisk; }

    public Map<String, Long> getCategoryWiseCount() { return categoryWiseCount; }
    public void setCategoryWiseCount(Map<String, Long> categoryWiseCount) { this.categoryWiseCount = categoryWiseCount; }
}
