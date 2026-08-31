package com.project.staynest.auth.persistence.db.jpa.model;

import com.project.staynest.auth.validation.Validation;

public record TokenIdentifier(
        String subject,
        String sessionId
) {
    public TokenIdentifier{
        String className = this.getClass().getSimpleName();
        Validation.validate(subject, "subject", className);
        Validation.validate(sessionId, "sessionId", className);
    }
}
