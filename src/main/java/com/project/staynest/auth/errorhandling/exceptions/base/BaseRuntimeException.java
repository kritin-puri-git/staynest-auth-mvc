package com.project.staynest.auth.errorhandling.exceptions.base;

import com.project.staynest.auth.errorhandling.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Map;

public abstract class BaseRuntimeException extends RuntimeException{
    public BaseRuntimeException(String message){
        super(message);
    }

    public abstract ErrorCode getErrorCode();
    public abstract String getClientMessage();
    public abstract HttpStatus getHttpStatus();
    public Map<String, String>  getErrorDetails(){
        return null;
    }

}
