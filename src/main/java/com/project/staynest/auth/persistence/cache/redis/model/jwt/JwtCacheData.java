package com.project.staynest.auth.persistence.cache.redis.model.jwt;

import com.project.staynest.auth.validation.Validation;

public record JwtCacheData(
        String subject,
        String sessionId,
        String accessTokenJti,
        String status,
        String deviceId,
        String userAgent,
        long ttl
) {
    public JwtCacheData {
        String className = this.getClass().getSimpleName();
        Validation.validate(subject, "subject", className);
        Validation.validate(sessionId, "sessionId", className);
        Validation.validate(accessTokenJti, "accessTokenJti", className);
        Validation.validate(status, "status", className);
        Validation.validate(deviceId, "deviceId", className);
        Validation.validate(userAgent, "userAgent", className);

        if(ttl <= 0)
            throw new IllegalArgumentException("ttl must be positive[" + className + "]");
    }
}
