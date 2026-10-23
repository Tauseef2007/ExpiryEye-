package com.expiryeye.controller;

import com.expiryeye.dto.ItemRequest;
import com.expiryeye.dto.ItemResponse;
import com.expiryeye.model.ItemCategory;
import com.expiryeye.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "ok", "database", "MySQL", "service", "ExpiryEye Backend"));
    }

    @PostMapping("/items")
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest request) {
        return new ResponseEntity<>(itemService.createItem(request), HttpStatus.CREATED);
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return ResponseEntity.ok(itemService.updateItem(id, request));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/items/expiring")
    public ResponseEntity<List<ItemResponse>> getExpiringItems(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(itemService.getExpiringItems(days));
    }

    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<ItemResponse>> getItemsByCategory(@PathVariable ItemCategory category) {
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }
}
