package com.project.staynest.auth.otp.model;

import com.project.staynest.auth.errorhandling.exceptions.unexpected.UnexpectedIllegalStateException;
import com.project.staynest.auth.otp.constants.OtpPolicyConstants;
import com.project.staynest.auth.validation.Validation;

public record VerifyOtpDetails(
        String purpose,
        String identifier,
        String token,
        String otp,
        int expiry
) {
    private static final int MIN_OTP_EXPIRY = OtpPolicyConstants.MIN_OTP_EXPIRY;

    public VerifyOtpDetails{
        String className = this.getClass().getSimpleName();

        Validation.validate(purpose, "purpose", className);
        Validation.validate(identifier, "identifier", className);
        Validation.validate(token, "token", className);
        Validation.validate(otp, "otp", className);

        if (expiry < MIN_OTP_EXPIRY){
            throw new UnexpectedIllegalStateException(
                    "Otp Expiry Cannot be less than " + MIN_OTP_EXPIRY + " seconds"
            );
        }
    }
}
