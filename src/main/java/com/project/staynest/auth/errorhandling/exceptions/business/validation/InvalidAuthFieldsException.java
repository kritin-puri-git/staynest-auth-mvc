package com.project.staynest.auth.errorhandling.exceptions.business.validation;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class InvalidAuthFieldsException extends BaseRuntimeException {

    private final Map<String, String> errorDetails;

    public InvalidAuthFieldsException(Map<String,String> errorDetails){
        super("Invalid Input Fields");
        this.errorDetails = errorDetails;
    }

    @Override
    public HttpStatus getHttpStatus(){
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public ErrorCode getErrorCode(){
        return ErrorCode.VALIDATION_FIELD_FAILED;
    }

    @Override
    public String getClientMessage(){
        return "Invalid Input Fields";
    }

    @Override
    public Map<String, String> getErrorDetails(){
        return this.errorDetails;
    }

}
