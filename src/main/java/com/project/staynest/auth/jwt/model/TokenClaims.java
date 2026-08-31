package com.project.staynest.auth.jwt.model;

import com.project.staynest.auth.validation.Validation;

import java.util.Date;
import java.util.List;


public record TokenClaims(
        String jwtTokenId,
        String subject,
        String sessionId,
        List<String> audience,
        String role,
        String jwtType,
        Date issueTime,
        Date expirationTime
) {
    public TokenClaims {
        String className = this.getClass().getSimpleName();
        Validation.validate(jwtTokenId, "jwtTokenId", className);
        Validation.validate(subject, "subject", className);
        Validation.validate(sessionId, "sessionId", className);
        Validation.validate(audience, "audience", className);
        Validation.validate(role, "role", className);
        Validation.validate(jwtType, "type", className);
        Validation.validate(issueTime, "issueTime", className);
        Validation.validate(expirationTime, "expirationTime", className);
        if(expirationTime.before(issueTime)) throw new IllegalArgumentException(
                "Expiration time must be after issue time[" + className + "]"
        );
    }
}
