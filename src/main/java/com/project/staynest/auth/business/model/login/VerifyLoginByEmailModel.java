package com.project.staynest.auth.business.model.login;

import com.project.staynest.auth.validation.Validation;

public record VerifyLoginByEmailModel(
        String email,
        String token,
        String otp,
        String deviceId,
        String userAgent
) {
    public VerifyLoginByEmailModel {
        String className = this.getClass().getSimpleName();

        Validation.validate(email, "identifier", className);
        Validation.validate(token, "token", className);
        Validation.validate(otp, "otp", className);
        Validation.validate(deviceId, "deviceId", className);
        Validation.validate(userAgent, "userAgent", className);

    }
}
