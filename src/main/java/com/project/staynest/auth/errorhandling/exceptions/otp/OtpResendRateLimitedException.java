package com.project.staynest.auth.errorhandling.exceptions.otp;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class OtpResendRateLimitedException extends BaseRuntimeException {

    public OtpResendRateLimitedException(){
        super("Detected multiple OTP resend requests with same kay within 5 sesonds ");
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.OTP_RESEND_RATE_LIMITED;
    }

    @Override
    public String getClientMessage() {
        return "Wait a few seconds before trying again.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.TOO_MANY_REQUESTS;
    }
}
