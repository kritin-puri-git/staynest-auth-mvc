package com.project.staynest.auth.errorhandling.exceptions.business.authentication;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseRuntimeException {
    public UserNotFoundException(){
        super("User not found while logging in");
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.USER_NOT_FOUND;
    }

    @Override
    public String getClientMessage() {
        return "Please try signing up first.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
