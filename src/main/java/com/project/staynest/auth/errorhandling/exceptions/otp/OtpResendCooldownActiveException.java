package com.project.staynest.auth.errorhandling.exceptions.otp;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class OtpResendCooldownActiveException extends BaseRuntimeException {
    public OtpResendCooldownActiveException(){
        super("OTP resend cooldown is active, can't resend OTP");
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.RESEND_COOLDOWN_ACTIVE;
    }

    @Override
    public String getClientMessage() {
        return "Please wait 30 seconds before requesting another OTP.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.TOO_MANY_REQUESTS;
    }
}
