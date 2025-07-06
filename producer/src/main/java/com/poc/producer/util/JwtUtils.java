package com.poc.producer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public String getTenantId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            logger.info("JWT Claims: {}", jwt.getClaims());
            // Assuming the tenant ID is stored in the "sub" claim
            return jwt.getClaimAsString("sub");
        }
        logger.warn("No JWT found in SecurityContext");
        return null;
    }
}
