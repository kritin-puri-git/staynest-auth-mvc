package com.project.staynest.auth.persistence.cache.redis.model.otp;

import com.project.staynest.auth.validation.Validation;

public record ResendOtpCacheData(
        String purpose,
        String identifier,
        String token,
        String hashedOtp,
        int maxVerifyAttempts,
        int resendCooldown,
        int ttl

) {

    public ResendOtpCacheData{
        String className = this.getClass().getSimpleName();
        Validation.validate(purpose, "purpose", className);
        Validation.validate(identifier, "identifier", className);
        Validation.validate(token, "token", className);
        Validation.validate(hashedOtp, "hashedOtp", className);

    }
}
