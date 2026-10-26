
package com.expiryeye.controller;

import com.expiryeye.model.ExpiryItem;
import com.expiryeye.model.ItemCategory;
import com.expiryeye.repository.ItemRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {
    "http://127.0.0.1:5500",
    "http://localhost:5500"
})
public class PaymentController {

    private final ItemRepository itemRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public PaymentController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    private boolean isEligible(ItemCategory category) {
        return category == ItemCategory.INSURANCE
                || category == ItemCategory.WARRANTY
                || category == ItemCategory.VEHICLE_DOCUMENT
                || category == ItemCategory.MOBILE_WIFI_RECHARGE;
    }

    private ResponseEntity<?> error(String message) {
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    // STEP 1: Create Razorpay order
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequest request) {
        try {
            if (request.itemId == null || request.userId == null) {
                return error("Item ID and user ID are required.");
            }

            Optional<ExpiryItem> optional =
                    itemRepository.findById(request.itemId);

            if (optional.isEmpty()) {
                return error("Item not found.");
            }

            ExpiryItem item = optional.get();

            // Check that this item belongs to the requesting user.
            if (item.getUser() == null
                    || !request.userId.equals(item.getUser().getId())) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Item access denied."));
            }

            if (!isEligible(item.getCategory())) {
                return error("Payment is not available for this category.");
            }

            if (item.getExpiryDate() == null
                    || !item.getExpiryDate().isBefore(LocalDate.now())) {
                return error("Only expired items can be renewed.");
            }

            Double value = item.getEstimatedValue();

            if (value == null || !Double.isFinite(value) || value <= 0) {
                return error("Item value must be greater than zero.");
            }

            // Estimated value is used as the demo payment amount.
            long amountPaise = Math.round(value * 100.0);

            if (amountPaise <= 0 || amountPaise > 100000000) {
                return error("Payment amount is outside the allowed range.");
            }

            RazorpayClient client =
                    new RazorpayClient(keyId, keySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt",
                    "item_" + item.getId() + "_" + System.currentTimeMillis());

            JSONObject notes = new JSONObject();
            notes.put("itemId", item.getId().toString());
            notes.put("userId", request.userId.toString());
            orderRequest.put("notes", notes);

            Order order = client.orders.create(orderRequest);

            Map<String, Object> result = new HashMap<>();
            result.put("keyId", keyId);
            result.put("orderId", order.get("id"));
            result.put("amount", amountPaise);
            result.put("currency", "INR");
            result.put("itemId", item.getId());
            result.put("itemName", item.getName());
            result.put("category", item.getCategory().name());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Could not create payment order."));
        }
    }

    // STEP 2: Verify Razorpay signature, then update expiry
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody PaymentVerification request) {

        try {
            if (request.itemId == null || request.userId == null
                    || isBlank(request.razorpay_order_id)
                    || isBlank(request.razorpay_payment_id)
                    || isBlank(request.razorpay_signature)) {
                return error("Payment verification details are incomplete.");
            }

            Optional<ExpiryItem> optional =
                    itemRepository.findById(request.itemId);

            if (optional.isEmpty()) {
                return error("Item not found.");
            }

            ExpiryItem item = optional.get();

            if (item.getUser() == null
                    || !request.userId.equals(item.getUser().getId())) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Item access denied."));
            }

            if (!isEligible(item.getCategory())) {
                return error("This category cannot be renewed through payment.");
            }

            // Prevent reusing a payment to renew an already renewed item.
            if (item.getExpiryDate() == null
                    || !item.getExpiryDate().isBefore(LocalDate.now())) {
                return error("Item is no longer expired.");
            }

            String signaturePayload =
                    request.razorpay_order_id + "|"
                    + request.razorpay_payment_id;

            boolean signatureValid = Utils.verifySignature(
                    signaturePayload,
                    request.razorpay_signature,
                    keySecret
            );

            if (!signatureValid) {
                return ResponseEntity.status(400)
                        .body(Map.of("error", "Invalid payment signature."));
            }

            // Set the new expiry date only after signature verification.
            if (item.getCategory() == ItemCategory.MOBILE_WIFI_RECHARGE) {
                item.setExpiryDate(LocalDate.now().plusDays(28));
            } else {
                item.setExpiryDate(LocalDate.now().plusDays(365));
            }

            itemRepository.save(item);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Payment verified and expiry date updated.");
            result.put("itemId", item.getId());
            result.put("newExpiryDate", item.getExpiryDate().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Payment verification failed."));
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static class PaymentRequest {
        public Long itemId;
        public Long userId;
    }

    public static class PaymentVerification {
        public Long itemId;
        public Long userId;
        public String razorpay_order_id;
        public String razorpay_payment_id;
        public String razorpay_signature;
    }
}
