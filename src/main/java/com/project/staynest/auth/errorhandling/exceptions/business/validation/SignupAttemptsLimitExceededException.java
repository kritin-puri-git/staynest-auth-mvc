package com.project.staynest.auth.errorhandling.exceptions.business.validation;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class SignupAttemptsLimitExceededException extends BaseRuntimeException {
    public SignupAttemptsLimitExceededException(){
        super("Too many signup attempts. Exceeds the attempts limit");
    }

    @Override
    public ErrorCode getErrorCode(){
        return ErrorCode.TOO_MANY_SIGNUP_ATTEMPTS;
    }

    @Override
    public String getClientMessage(){
        return "Please try again after few minutes.";
    }

    @Override
    public HttpStatus getHttpStatus(){
        return HttpStatus.TOO_MANY_REQUESTS;
    }
}
