package com.project.staynest.auth.persistence.db.jpa.model;

import com.project.staynest.auth.validation.Validation;

import java.time.Instant;

public record RefreshTokenData(
        String refreshJwtTokenId,
        Instant refreshExpirationTime,
        String status,
        String role,
        String deviceId,
        String userAgent
) {
    public RefreshTokenData {
        String className = this.getClass().getSimpleName();
        Validation.validate(refreshJwtTokenId, "refreshJwtTokenId", className);
        Validation.validate(status, "status", className);
        Validation.validate(role, "role", className);
        Validation.validate(refreshExpirationTime, "refreshExpirationTime", className);
        Validation.validate(deviceId, "deviceId", className);
        Validation.validate(userAgent, "userAgent", className);
    }
}