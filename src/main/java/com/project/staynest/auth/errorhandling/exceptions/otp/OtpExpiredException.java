package com.project.staynest.auth.errorhandling.exceptions.otp;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class OtpExpiredException extends BaseRuntimeException {

    public OtpExpiredException(){
        super("Otp Session Expired");
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.OTP_EXPIRED;
    }

    @Override
    public String getClientMessage() {
        return "OTP session expired. Please sign up again.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
