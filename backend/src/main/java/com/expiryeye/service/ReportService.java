package com.expiryeye.service;

import com.expiryeye.dto.DashboardResponse;
import com.expiryeye.dto.ItemResponse;
import com.expiryeye.model.ItemStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
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
    // PDF Export Logic
    public ByteArrayInputStream generatePdfReport() {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("ExpiryEye - Household Safety & Expiry Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{3, 2, 2, 2, 2});

            // Table Headers
            String[] headers = {"Item Name", "Category", "Expiry Date", "Days Left", "Status"};
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                cell.setBackgroundColor(BaseColor.GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Table Data
            List<ItemResponse> items = itemService.getAllItems();
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            for (ItemResponse item : items) {
                table.addCell(new PdfPCell(new Phrase(item.getName(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(item.getCategory().name(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(item.getExpiryDate().toString(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getDaysRemaining()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(item.getStatus().name(), dataFont)));
            }

            document.add(table);
            document.close();

        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
