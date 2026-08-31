package com.project.staynest.auth.errorhandling.exceptions.unexpected;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseIllegalStateException;
import org.springframework.http.HttpStatus;

public class UnexpectedIllegalStateException extends BaseIllegalStateException {

    public UnexpectedIllegalStateException(String reason, Exception exception){
        super(reason, exception);
    }
    public UnexpectedIllegalStateException(String reason){
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
