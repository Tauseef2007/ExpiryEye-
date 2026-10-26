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

    // EmailService.java ke andar yeh naya method jodein:
    public void sendExpiredAlert(String toEmail, String itemName, String expiryDate) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject("🚨 EXPIRED ALERT: " + itemName + " has expired!");
    message.setText("Hello,\n\n" +
            "This is an urgent safety notification from ExpiryEye.\n\n" +
            "Item: " + itemName + "\n" +
            "Expiry Date was: " + expiryDate + "\n\n" +
            "⚠️ This item has EXPIRED! Please dispose of it immediately to prevent health hazards or financial loss.\n\n" +
            "Best regards,\nExpiryEye Team");

    mailSender.send(message);
}

    // EmailService.java mein yeh naya method add karein:
    
        public void sendReorderAlert(
        String toEmail,
        String itemName,
        String expiryDate,
        Long itemId,
        com.expiryeye.model.ItemCategory category) {

    try {
        jakarta.mail.internet.MimeMessage message =
                mailSender.createMimeMessage();

        org.springframework.mail.javamail.MimeMessageHelper helper =
                new org.springframework.mail.javamail.MimeMessageHelper(
                        message, true, "UTF-8");

        String subject;
        String description;
        String buttonText;
        String buttonEmoji;

        switch (category == null
                ? com.expiryeye.model.ItemCategory.OTHER
                : category) {

            case INSURANCE:
                subject = "Insurance Expired: " + itemName
                        + " - Renewal Reminder";
                description = "Your insurance policy has expired. ";
                buttonText = "Renew Policy";
                buttonEmoji = "🛡️";
                break;

            case VEHICLE_DOCUMENT:
                subject = "Vehicle Document Expired: " + itemName;
                description = "Your vehicle document has expired. "
                        + "Please check the applicable renewal process.";
                buttonText = "Renew Document";
                buttonEmoji = "📄";
                break;

            case WARRANTY:
                subject = "Warranty Expired: " + itemName;
                description = "Your warranty has expired. "
                        + "Check with the provider for renewal or extension options.";
                buttonText = "Check Warranty Options";
                buttonEmoji = "🛠️";
                break;

            case MOBILE_WIFI_RECHARGE:
                subject = "Recharge Reminder: " + itemName;
                description = "Your mobile or Wi-Fi recharge validity "
                        + "has expired. Recharge to continue your service.";
                buttonText = "Recharge Now";
                buttonEmoji = "📱";
                break;

            case GROCERY:
                subject = "Restock Reminder: " + itemName;
                description = "This item has passed its expiry date. "
                        + "Check the product condition and replace it if needed.";
                buttonText = "Review Item";
                buttonEmoji = "🛒";
                break;

            case MEDICINE:
                subject = "Medicine Expiry Alert: " + itemName;
                description = "This medicine has passed its expiry date. "
                        + "Do not use expired medicine. Consult a pharmacist "
                        + "about safe disposal and replacement.";
                buttonText = "Review Item";
                buttonEmoji = "💊";
                break;

            default:
                subject = "Expired Item Reminder: " + itemName;
                description = "This item has passed its expiry date. "
                        + "Please review it and decide what action is appropriate.";
                buttonText = "Review Item";
                buttonEmoji = "📋";
                break;
        }

        helper.setTo(toEmail);
        helper.setSubject(subject);

        String actionUrl =
                "http://127.0.0.1:5500/ExpiryEye/frontend/payment.html?itemId="
        + itemId;

        String htmlContent =
                "<h3>Hello,</h3>"
                + "<p>Your item <b>" + itemName
                + "</b> has passed its expiry date: <b>"
                + expiryDate + "</b>.</p>"
                + "<p>" + description + "</p>"
                + "<br>"
                + "<a href='" + actionUrl
                + "' style='background-color:#16a34a;color:white;"
                + "padding:12px 20px;text-decoration:none;"
                + "border-radius:6px;font-weight:bold;'>"
                + buttonEmoji + " " + buttonText + "</a>"
                + "<br><br>"
                + "<p>Best regards,<br>ExpiryEye Team</p>";

        helper.setText(htmlContent, true);
        mailSender.send(message);

    } catch (Exception e) {
        e.printStackTrace();
    }
}
   
}