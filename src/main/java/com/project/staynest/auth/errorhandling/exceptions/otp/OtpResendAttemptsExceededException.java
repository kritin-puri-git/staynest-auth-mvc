package com.project.staynest.auth.errorhandling.exceptions.otp;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class OtpResendAttemptsExceededException extends BaseRuntimeException {
    public OtpResendAttemptsExceededException(){
        super("OTP Resend attempts exceeded");
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.TOO_MANY_OTP_RESEND_ATTEMPTS;
    }

    @Override
    public String getClientMessage() {
        return "Maximum OTP resend attempts reached.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.TOO_MANY_REQUESTS;
    }
}
