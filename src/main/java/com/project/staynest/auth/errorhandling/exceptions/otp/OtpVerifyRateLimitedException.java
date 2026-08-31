package com.project.staynest.auth.errorhandling.exceptions.otp;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class OtpVerifyRateLimitedException extends BaseRuntimeException {

    public OtpVerifyRateLimitedException(){
        super("Detected multiple OTP verify requests with same kay within 5 sesonds ");
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.OTP_VERIFY_RATE_LIMITED;
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
