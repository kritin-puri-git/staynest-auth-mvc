package com.project.staynest.auth.business.model.jwt;

import com.project.staynest.auth.validation.Validation;

public record JwtGenerationResult(
        String accessToken,
        String refreshToken,
        String deviceId,
        long refreshTokenAge
) {
    public JwtGenerationResult{
        String className = this.getClass().getSimpleName();
        Validation.validate(accessToken, "accessToken", className);
        Validation.validate(refreshToken, "refreshToken", className);
        Validation.validate(deviceId, "deviceId", className);
    }
}
