package com.project.staynest.auth.persistence.db.jpa.model;

import com.project.staynest.auth.validation.Validation;

import java.time.Instant;

public record JwtTokenData(
        String subject,
        String accessJwtTokenId,
        String refreshJwtTokenId,
        String sessionId,
        Instant refreshExpirationTime,
        String status,
        String role,
        String deviceId,
        String userAgent
) {
    public JwtTokenData {
        String className = this.getClass().getSimpleName();
        Validation.validate(subject, "subject", className);
        Validation.validate(accessJwtTokenId, "accessJwtTokenId", className);
        Validation.validate(refreshJwtTokenId, "refreshJwtTokenId", className);
        Validation.validate(sessionId, "sessionId", className);
        Validation.validate(status, "status", className);
        Validation.validate(role, "role", className);
        Validation.validate(refreshExpirationTime, "refreshExpirationTime", className);
        if(!refreshExpirationTime.isAfter(Instant.now())) throw new IllegalArgumentException(
                "Refresh expiration time must be after current time[" + className + "]"
        );
        Validation.validate(deviceId, "deviceId", className);
        Validation.validate(userAgent, "userAgent", className);
    }
}