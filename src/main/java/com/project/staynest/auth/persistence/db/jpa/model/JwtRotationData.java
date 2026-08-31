package com.project.staynest.auth.persistence.db.jpa.model;

import com.project.staynest.auth.validation.Validation;

import java.time.Instant;

public record JwtRotationData(
        String subject,
        String sessionId,
        String accessJwtTokenId,
        String refreshJwtTokenId,
        Instant refreshExpirationTime
) {
    public JwtRotationData {
        String className = this.getClass().getSimpleName();
        Validation.validate(subject, "subject", className);
        Validation.validate(accessJwtTokenId, "accessJwtTokenId", className);
        Validation.validate(refreshJwtTokenId, "refreshJwtTokenId", className);
        Validation.validate(sessionId, "sessionId", className);
        Validation.validate(refreshExpirationTime, "refreshExpirationTime", className);
        if(!refreshExpirationTime.isAfter(Instant.now())) throw new IllegalArgumentException(
                "Refresh expiration time must be after current time[" + className + "]"
        );
    }
}
