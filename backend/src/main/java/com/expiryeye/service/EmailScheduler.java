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

    // Har din subah 9 baje yeh function apne aap chalega
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkAndSendExpiryAlerts() {
        List<ExpiryItem> items = itemRepository.findAll();
        LocalDate today = LocalDate.now();

        for (ExpiryItem item : items) {
            if (item.getExpiryDate() != null) {
                long daysLeft = ChronoUnit.DAYS.between(today, item.getExpiryDate());

                // Agar item next 7 days ke andar expire ho raha hai
                if (daysLeft >= 0 && daysLeft <= 7) {
                    String recipientEmail = "tk2310984@gmail.com"; // Default alert address

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