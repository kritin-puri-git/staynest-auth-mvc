package com.project.staynest.auth.errorhandling.exceptions.business.validation;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class SignupRequestCannotBeNullException extends BaseRuntimeException {

    public SignupRequestCannotBeNullException(){
        super("SignupRequestDto Cannot be Null");
    }

    @Override
    public HttpStatus getHttpStatus(){
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public ErrorCode getErrorCode(){
        return ErrorCode.VALIDATION_FAILED;
    }

    @Override
    public String getClientMessage(){
        return "Invalid Request";
    }

}