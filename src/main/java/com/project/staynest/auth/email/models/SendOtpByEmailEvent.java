package com.project.staynest.auth.email.models;

import com.project.staynest.auth.validation.Validation;

public record SendOtpByEmailEvent(
        String email,
        String otp,
        String purpose
) {
    public SendOtpByEmailEvent{
        String className = this.getClass().getSimpleName();

        Validation.validate(email, "identifier", className);
        Validation.validate(otp, "otp", className);
        Validation.validate(purpose, "purpose", className);
    }

}
