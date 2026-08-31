package com.project.staynest.auth.errorhandling.exceptions.otp;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class OtpNotMatchedException extends BaseRuntimeException {
    public OtpNotMatchedException(){
        super("Provided OTP did not match the generated OTP");
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.OTP_NOT_MATCHED;
    }

    @Override
    public String getClientMessage() {
        return "Invalid OTP";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
