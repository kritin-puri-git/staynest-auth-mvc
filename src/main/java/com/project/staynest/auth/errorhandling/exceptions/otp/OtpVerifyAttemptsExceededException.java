package com.project.staynest.auth.errorhandling.exceptions.otp;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class OtpVerifyAttemptsExceededException extends BaseRuntimeException {

    public OtpVerifyAttemptsExceededException(){
        super("OTP verification attempts exceeded");
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.TOO_MANY_OTP_VERIFY_ATTEMPTS;
    }

    @Override
    public String getClientMessage() {
        return "Verification failed due to too many attempts.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.TOO_MANY_REQUESTS;
    }
}