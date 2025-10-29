package com.testing.ex.utils;

import com.testing.ex.security.TestingUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtil {
    public static String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof TestingUserDetails user) {
            return user.getUser().getId(); // Assuming TestingUser has getUser()
        }
        return null;
    }
}