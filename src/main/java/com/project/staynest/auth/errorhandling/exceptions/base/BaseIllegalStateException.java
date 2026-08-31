package com.project.staynest.auth.errorhandling.exceptions.base;

import com.project.staynest.auth.errorhandling.ErrorCode;
import org.springframework.http.HttpStatus;

public abstract class BaseIllegalStateException extends IllegalStateException{

    public BaseIllegalStateException(String message, Exception exception){
        super(message, exception);
    }

    public BaseIllegalStateException(String message){
        super(message);
    }

    public  abstract ErrorCode getErrorCode();
    public  abstract String getClientMessage();
    public  abstract HttpStatus getHttpStatus();
}


