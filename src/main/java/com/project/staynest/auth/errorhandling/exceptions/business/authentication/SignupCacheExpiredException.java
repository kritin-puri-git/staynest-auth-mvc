package com.project.staynest.auth.errorhandling.exceptions.business.authentication;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class SignupCacheExpiredException extends BaseRuntimeException {
    public SignupCacheExpiredException(){
        super("OTP verified but signup cache missing or expired");
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.SIGNUP_CACHE_EXPIRED;
    }

    @Override
    public String getClientMessage() {
        return "Signup session expired. Please signup again.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.GONE;
    }
}
