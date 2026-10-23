package com.expiryeye.controller;

import com.expiryeye.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
//PDF UPDATION IMPORTS!!
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayInputStream;
//EMAIL ALERTS UPDATION!!
import com.expiryeye.service.EmailService;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;
    private final EmailService emailService;

    public ReportController(ReportService reportService, EmailService emailService) {
        this.reportService = reportService;
        this.emailService = emailService;
    }

    @GetMapping("/reports/waste")
    public ResponseEntity<Map<String, Object>> getWasteReport() {
        return ResponseEntity.ok(reportService.getWasteReport());
    }
// PDF UPDATION!!
@GetMapping(value = "/reports/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadPdfReport() {
        ByteArrayInputStream bis = reportService.generatePdfReport();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=ExpiryEye_Report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
@GetMapping("/test-email")
    public String sendTestEmail(@RequestParam String toEmail) {
        emailService.sendExpiryAlert(toEmail, "Dolo 650 (Test Medicine)", "2026-11-01", 8);
        return "Email alert successfully sent to: " + toEmail;
    }
}
