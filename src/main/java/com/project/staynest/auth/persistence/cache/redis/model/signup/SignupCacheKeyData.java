package com.project.staynest.auth.persistence.cache.redis.model.signup;

import com.project.staynest.auth.validation.Validation;

public record SignupCacheKeyData(
        String identifier,
        String token
) {
    public SignupCacheKeyData {
        String className = this.getClass().getSimpleName();
        Validation.validate(identifier, "identifier", className);
        Validation.validate(token, "token", className);

    }
}
