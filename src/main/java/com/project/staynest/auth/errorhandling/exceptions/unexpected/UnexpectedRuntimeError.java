package com.project.staynest.auth.errorhandling.exceptions.unexpected;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class UnexpectedRuntimeError extends BaseRuntimeException {

    public UnexpectedRuntimeError(String reason){
        super(reason);
    }

    @Override
    public ErrorCode getErrorCode(){
        return ErrorCode.UNEXPECTED_ERROR;
    }

    @Override
    public String getClientMessage(){
        return "Something went Wrong";
    }

    @Override
    public HttpStatus getHttpStatus(){
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
