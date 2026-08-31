package com.project.staynest.auth.business.model;

import com.project.staynest.auth.validation.Validation;

public record SentOtpData(
        String email,
        String token
) {
    public SentOtpData{
        String className = this.getClass().getSimpleName();
        Validation.validate(email, "email", className);
        Validation.validate(token, "token", className);
    }
}