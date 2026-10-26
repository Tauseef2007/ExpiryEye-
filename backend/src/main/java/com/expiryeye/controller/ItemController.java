package com.expiryeye.controller;

import com.expiryeye.model.ExpiryItem;
import com.expiryeye.model.User;
import com.expiryeye.repository.ItemRepository;
import com.expiryeye.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*") // Frontend CORS support
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Get all items for a specific logged-in user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpiryItem>> getItemsByUser(@PathVariable Long userId) {
        List<ExpiryItem> items = itemRepository.findByUserId(userId);
        return ResponseEntity.ok(items);
    }

    // 2. Add new item linked to user
    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ExpiryItem item) {
    if (item.getUser() == null || item.getUser().getId() == null) {
        return ResponseEntity.badRequest().body("User ID is missing in request!");
    }

    User user = userRepository.findById(item.getUser().getId()).orElse(null);
    if (user == null) {
        return ResponseEntity.badRequest().body("User not found in database!");
    }



    item.setUser(user);



    // Category ke mutabiq automatic reminder days set karna
    if (item.getCategory() != null) {
        String cat = item.getCategory().name();
        if (cat.equals("INSURANCE") || cat.equals("WARRANTY") || cat.equals("VEHICLE_DOCUMENT")) {
            item.setReminderDays(30);
        } else if (cat.equals("MOBILE_WIFI_RECHARGE")) {
            item.setReminderDays(3);
        } else {
            item.setReminderDays(7);
        }
    } else {
        item.setReminderDays(7);
    }

    // Agar quantity null ho toh default 1 set kar dena
    if (item.getQuantity() == null || item.getQuantity() <= 0) {
        item.setQuantity(1);
    }

    ExpiryItem savedItem = itemRepository.save(item);
    return ResponseEntity.ok(savedItem);
    }

    
    // Update an existing item by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long id,
            @RequestBody ExpiryItem updatedItem) {

        Optional<ExpiryItem> optionalItem = itemRepository.findById(id);

        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ExpiryItem existingItem = optionalItem.get();

        existingItem.setName(updatedItem.getName());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setExpiryDate(updatedItem.getExpiryDate());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setEstimatedValue(updatedItem.getEstimatedValue());
        existingItem.setNotes(updatedItem.getNotes());

        ExpiryItem savedItem = itemRepository.save(existingItem);

        return ResponseEntity.ok(savedItem);
    }


    // 3. Delete an item by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        itemRepository.deleteById(id);
        return ResponseEntity.ok().body("Item deleted successfully!");
    }

    // 4. Smart Reorder / Renew Endpoint (Triggered via Email Click)
    @GetMapping("/reorder/{id}")
    public ResponseEntity<String> reorderItem(@PathVariable Long id) {
        Optional<ExpiryItem> optionalItem = itemRepository.findById(id);
        
        if (optionalItem.isPresent()) {
            ExpiryItem item = optionalItem.get();
            
            // Expiry date ko 30 din aage badhana
            item.setExpiryDate(LocalDate.now().plusDays(30));
            itemRepository.save(item);
            
            // Default text (Grocery aur Medicine ke liye)
            String actionText = "Restocked & Refilled! 🛒";
            String descText = "has been restocked and its expiry date has been extended by 30 days.";
            
            if (item.getCategory() != null) {
                String cat = item.getCategory().name();
                
                // Insurance, Warranty, Vehicle Documents ke liye
                if (cat.equals("INSURANCE") || cat.equals("WARRANTY") || cat.equals("VEHICLE_DOCUMENT")) {
                    item.setExpiryDate(LocalDate.now().plusDays(365));
                    actionText = "Renewed Successfully! 🛡️";
                    descText = "has been successfully renewed and its validity has been extended by 1 Year.";
                }
                // 2. Mobile & WiFi Recharge ke liye
                    else if (cat.equals("MOBILE_WIFI_RECHARGE")) {
                    item.setExpiryDate(LocalDate.now().plusDays(28));
                    actionText = "Recharged Successfully! 📱";
                    descText = "has been successfully recharged and its validity has been extended by 28 days.";
                }
                // Others category ke liye
                else if (cat.equals("OTHER")) {
                    item.setExpiryDate(LocalDate.now().plusDays(30));
                    actionText = "Updated & Extended! 📦";
                    descText = "has been updated and its expiry date has been extended successfully by 1 Month.";
                }
            }
            
            String htmlResponse = "<html><body style='text-align:center; font-family:sans-serif; margin-top:50px;'>" +
                    "<h1 style='color:#22c55e;'>" + actionText + "</h1>" +
                    "<p><b>" + item.getName() + "</b> (" + item.getCategory() + ") " + descText + "</p>" +
                    "<p>You can now close this tab and return to your ExpiryEye Dashboard.</p>" +
                    "</body></html>";
                    
            return ResponseEntity.ok(htmlResponse);
        }
        
        return ResponseEntity.badRequest().body("Item not found!");
    }
}