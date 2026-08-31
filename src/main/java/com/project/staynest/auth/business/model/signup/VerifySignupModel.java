package com.project.staynest.auth.business.model.signup;

import com.project.staynest.auth.validation.Validation;

public record VerifySignupModel(
        String email,
        String token,
        String otp,
        String deviceId,
        String userAgent
) {
    public VerifySignupModel{

        String className = this.getClass().getSimpleName();

        Validation.validate(email, "identifier", className);
        Validation.validate(token, "token", className);
        Validation.validate(otp, "otp", className);
        Validation.validate(deviceId, "deviceId", className);
        Validation.validate(userAgent, "userAgent", className);

    }

}
