package com.project.staynest.auth.otp.model;

import com.project.staynest.auth.errorhandling.exceptions.unexpected.UnexpectedIllegalStateException;
import com.project.staynest.auth.otp.constants.OtpPolicyConstants;
import com.project.staynest.auth.validation.Validation;
import jakarta.validation.constraints.Email;

public record EmailOtpData(
        String purpose,
        @Email
        String email,
        String token,
        int expiry,
        int ttl
) {
    private static final int MIN_OTP_EXPIRY = OtpPolicyConstants.MIN_OTP_EXPIRY;
    private static final int MIN_OTP_TTL = OtpPolicyConstants.MIN_OTP_TTL;

    public EmailOtpData{
        String className = this.getClass().getSimpleName();

        Validation.validate(purpose, "purpose", className);
        Validation.validate(token, "token", className);
        Validation.validate(email, "identifier", className);

        if (expiry < MIN_OTP_EXPIRY){
            throw new UnexpectedIllegalStateException(
                    "Otp Expiry Cannot be less than " + MIN_OTP_EXPIRY + " seconds"
            );
        }

        if (ttl < MIN_OTP_TTL){
            throw new UnexpectedIllegalStateException(
                    "Otp TTL Cannot be less than " + MIN_OTP_TTL + " seconds"
            );
        }

        email = email.trim();
        purpose = purpose.trim();
        token = token.trim();
    }
}
