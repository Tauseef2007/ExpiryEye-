package com.expiryeye.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendExpiryAlert(String toEmail, String itemName, String expiryDate, long daysLeft) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("⚠️ Expiry Alert: " + itemName + " is expiring soon!");
        message.setText("Hello,\n\nThis is an automated safety alert from ExpiryEye.\n\n" +
                "Item: " + itemName + "\n" +
                "Expiry Date: " + expiryDate + "\n" +
                "Days Remaining: " + daysLeft + " day(s)\n\n" +
                "Please take action before it expires to prevent health hazards or money loss.\n\n" +
                "Best regards,\nExpiryEye Team");

        mailSender.send(message);
    }
}