package com.expiryeye.service;

import com.expiryeye.model.ExpiryItem;
import com.expiryeye.repository.ItemRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class EmailScheduler {

    private final ItemRepository itemRepository;
    private final EmailService emailService;

    public EmailScheduler(ItemRepository itemRepository, EmailService emailService) {
        this.itemRepository = itemRepository;
        this.emailService = emailService;
    }

    
    @Scheduled(cron = "0 0 9 * * ?") // Har din subah 9 baje yeh automatic chalega
    //@Scheduled(cron = "0 * * * * ?") // Har 1 minute mein chalega
    public void checkAndSendExpiryAlerts() {
        List<ExpiryItem> items = itemRepository.findAll();
        LocalDate today = LocalDate.now();

        for (ExpiryItem item : items) {
            if (item.getExpiryDate() != null && item.getUser() != null && item.getUser().getEmail() != null) {
                long daysLeft = ChronoUnit.DAYS.between(today, item.getExpiryDate());
                String recipientEmail = item.getUser().getEmail();

                // 1. YAHAN HAI STEP 2: Agar item expire ho chuka hai (Days < 0)
                if (daysLeft < 0) {
                    // Yahan hum naya HTML reorder alert bhej rahe hain jisme Item ki ID bhi hai
                    emailService.sendReorderAlert(
                        recipientEmail,
                        item.getName(),
                        item.getExpiryDate().toString(),
                        item.getId(),
                        item.getCategory()
                    );
                }
                // 2. Agar item agle 7 din ke andar expire hone wala hai
                else if (daysLeft <= 7) {
                    emailService.sendExpiryAlert(
                        recipientEmail,
                        item.getName(),
                        item.getExpiryDate().toString(),
                        daysLeft
                    );
                }
            }
        }
    }
}