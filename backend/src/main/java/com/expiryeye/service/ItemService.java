package com.expiryeye.service;

import com.expiryeye.dto.ItemRequest;
import com.expiryeye.dto.ItemResponse;
import com.expiryeye.exception.ItemNotFoundException;
import com.expiryeye.model.ExpiryItem;
import com.expiryeye.model.ItemCategory;
import com.expiryeye.model.ItemStatus;
import com.expiryeye.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ExpiryCalculationService calculationService;

    public ItemService(ItemRepository itemRepository, ExpiryCalculationService calculationService) {
        this.itemRepository = itemRepository;
        this.calculationService = calculationService;
    }

    public ItemResponse createItem(ItemRequest request) {
        ExpiryItem item = new ExpiryItem(
                request.getName(),
                request.getCategory(),
                request.getExpiryDate(),
                request.getReminderDays(),
                request.getOwnerName(),
                request.getEstimatedValue(),
                request.getNotes()
        );
        ExpiryItem saved = itemRepository.save(item);
        return mapToResponse(saved, LocalDate.now());
    }

    public List<ItemResponse> getAllItems() {
        LocalDate today = LocalDate.now();
        return itemRepository.findAll().stream()
                .map(item -> mapToResponse(item, today))
                .collect(Collectors.toList());
    }

    public ItemResponse getItemById(Long id) {
        ExpiryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
        return mapToResponse(item, LocalDate.now());
    }

    public ItemResponse updateItem(Long id, ItemRequest request) {
        ExpiryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));

        item.setName(request.getName());
        item.setCategory(request.getCategory());
        item.setExpiryDate(request.getExpiryDate());
        item.setReminderDays(request.getReminderDays());
        item.setOwnerName(request.getOwnerName());
        item.setEstimatedValue(request.getEstimatedValue());
        item.setNotes(request.getNotes());

        ExpiryItem updated = itemRepository.save(item);
        return mapToResponse(updated, LocalDate.now());
    }

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    public List<ItemResponse> getExpiringItems(int days) {
        LocalDate targetDate = LocalDate.now().plusDays(days);
        return itemRepository.findByExpiryDateLessThanEqual(targetDate).stream()
                .map(item -> mapToResponse(item, LocalDate.now()))
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getItemsByCategory(ItemCategory category) {
        return itemRepository.findByCategory(category).stream()
                .map(item -> mapToResponse(item, LocalDate.now()))
                .collect(Collectors.toList());
    }

    public ItemResponse mapToResponse(ExpiryItem item, LocalDate currentDate) {
        ItemResponse resp = new ItemResponse();
        resp.setId(item.getId());
        resp.setName(item.getName());
        resp.setCategory(item.getCategory());
        resp.setExpiryDate(item.getExpiryDate());
        resp.setReminderDays(item.getReminderDays());
        resp.setOwnerName(item.getOwnerName());
        resp.setEstimatedValue(item.getEstimatedValue());
        resp.setNotes(item.getNotes());

        long days = calculationService.calculateDaysRemaining(item.getExpiryDate(), currentDate);
        ItemStatus status = calculationService.calculateStatus(item.getExpiryDate(), item.getReminderDays(), currentDate);

        resp.setDaysRemaining(days);
        resp.setStatus(status);
        resp.setSafetyWarning(calculationService.getSafetyWarning(item.getCategory(), status));

        return resp;
    }
}
