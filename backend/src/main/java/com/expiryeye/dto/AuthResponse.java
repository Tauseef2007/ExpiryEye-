package com.expiryeye.dto;

/**
 * Safe authentication response. Deliberately excludes the user's password.
 */
public class AuthResponse {
    private final Long id;
    private final String name;
    private final String email;

    public AuthResponse(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
