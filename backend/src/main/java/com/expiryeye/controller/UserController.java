package com.expiryeye.controller;

import com.expiryeye.dto.AuthResponse;
import com.expiryeye.model.User;
import com.expiryeye.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Name, email and password are required.");
        }

        user.setEmail(user.getEmail().trim().toLowerCase());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already registered!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(toAuthResponse(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()
                || loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Email and password are required.");
        }

        User user = userRepository.findByEmail(loginRequest.getEmail().trim().toLowerCase())
                .orElse(null);

        if (user == null || !verifyAndUpgradePassword(user, loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Invalid email or password!");
        }

        return ResponseEntity.ok(toAuthResponse(user));
    }

    /**
     * Supports existing accounts that were stored as plain text by the old
     * version. On the first successful login, their password is upgraded to
     * BCrypt without forcing users to reset their password.
     */
    private boolean verifyAndUpgradePassword(User user, String suppliedPassword) {
        String storedPassword = user.getPassword();
        if (storedPassword == null) {
            return false;
        }

        if (storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(suppliedPassword, storedPassword);
        }

        if (storedPassword.equals(suppliedPassword)) {
            user.setPassword(passwordEncoder.encode(suppliedPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(user.getId(), user.getName(), user.getEmail());
    }
}
