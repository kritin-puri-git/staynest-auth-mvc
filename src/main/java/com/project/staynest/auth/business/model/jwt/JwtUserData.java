package com.project.staynest.auth.business.model.jwt;

import com.project.staynest.auth.validation.Validation;

import java.util.List;

public record JwtUserData(
        String subject,
        String sessionId,
        String role,
        String status,
        List<String> audience,
        String deviceId,
        String userAgent
) {
    public JwtUserData {
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
