package com.project.staynest.auth.persistence.cache.redis.model.otp;

import com.project.staynest.auth.validation.Validation;

public record OtpCacheMetadata(
        String purpose,
        String identifier,
        String token,
        int expiry
) {
    public OtpCacheMetadata{
        String className = this.getClass().getSimpleName();
        Validation.validate(purpose, "purpose", className);
        Validation.validate(identifier, "identifier", className);
        Validation.validate(token, "token", className);

    }
}
