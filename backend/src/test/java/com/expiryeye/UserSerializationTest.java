package com.expiryeye;

import com.expiryeye.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSerializationTest {

    @Test
    void userJsonDoesNotExposePassword() throws Exception {
        User user = new User("Test User", "test@example.com", "not-a-real-password");

        String json = new ObjectMapper().writeValueAsString(user);

        assertTrue(json.contains("test@example.com"));
        assertFalse(json.contains("password"));
        assertFalse(json.contains("not-a-real-password"));
    }
}
