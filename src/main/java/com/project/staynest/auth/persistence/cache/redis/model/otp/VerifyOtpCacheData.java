package com.project.staynest.auth.persistence.cache.redis.model.otp;

import com.project.staynest.auth.validation.Validation;

public record VerifyOtpCacheData(
        String purpose,
        String identifier,
        String token,
        String hashedOtp
) {

    public VerifyOtpCacheData{
        String className = this.getClass().getSimpleName();
        Validation.validate(purpose, "purpose", className);
        Validation.validate(identifier, "identifier", className);
        Validation.validate(token, "token", className);
        Validation.validate(hashedOtp, "hashedOtp", className);

    }
}
