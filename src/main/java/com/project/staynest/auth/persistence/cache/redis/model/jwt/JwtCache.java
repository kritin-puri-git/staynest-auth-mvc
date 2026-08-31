package com.project.staynest.auth.persistence.cache.redis.model.jwt;

import com.project.staynest.auth.validation.Validation;

public record JwtCache(
        String accessTokenJti,
        String status,
        String deviceId,
        String userAgent
) {
    public JwtCache {
        String className = this.getClass().getSimpleName();
        Validation.validate(accessTokenJti, "accessTokenJti", className);
        Validation.validate(status, "status", className);
        Validation.validate(deviceId, "deviceId", className);
        Validation.validate(userAgent, "userAgent", className);

        }
}
