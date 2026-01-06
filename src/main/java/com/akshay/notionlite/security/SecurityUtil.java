package com.akshay.notionlite.security;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static UUID currentUserId() {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (p instanceof JwtPrincipal jp) return jp.userId();
        throw new IllegalStateException("No authenticated principal");
    }
}
