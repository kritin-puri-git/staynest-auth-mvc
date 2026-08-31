package com.project.staynest.auth.security.model;

import com.project.staynest.auth.validation.Validation;

import java.util.List;

public record JwtIdentity(
        String subject,
        String sessionId,
        String role,
        String status,
        List<String> audience,
        String deviceId,
        String userAgent
) {
    public JwtIdentity {
        String className = this.getClass().getSimpleName();
        Validation.validate(subject, "subject", className);
        Validation.validate(sessionId, "sessionId", className);
        Validation.validate(role, "role", className);
        Validation.validate(status, "status", className);
        Validation.validate(audience, "audience", className);
        Validation.validate(sessionId, "sessionId", className);
        Validation.validate(deviceId, "deviceId", className);
        Validation.validate(userAgent, "userAgent", className);
    }
}