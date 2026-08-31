package com.project.staynest.auth.errorhandling.exceptions.business.authentication;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class UserNotFoundAfterVerificationException extends BaseRuntimeException {
    public UserNotFoundAfterVerificationException(){
        super("User not found after successful OTP verification");
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.USER_NOT_FOUND_AFTER_OTP_VERIFIED;
    }

    @Override
    public String getClientMessage() {
        return "Something went wrong. Please try logging in again";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
