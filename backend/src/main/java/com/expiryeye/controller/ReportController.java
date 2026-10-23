package com.expiryeye.controller;

import com.expiryeye.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports/waste")
    public ResponseEntity<Map<String, Object>> getWasteReport() {
        return ResponseEntity.ok(reportService.getWasteReport());
    }
}
