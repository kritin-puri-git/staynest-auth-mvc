package com.project.staynest.auth.errorhandling.exceptions.business.validation;

import com.project.staynest.auth.errorhandling.ErrorCode;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class UsernameAlreadyExistsException extends BaseRuntimeException {
    public UsernameAlreadyExistsException(){
        super("Username Already Exists in Database");
    }

    @Override
    public ErrorCode getErrorCode(){
        return ErrorCode.EMAIL_ALREADY_EXISTS;
    }

    @Override
    public String getClientMessage(){
        return "An account with this username already exists";
    }

    @Override
    public Map<String, String> getErrorDetails(){
        return new HashMap<>(Map.of("username", "Try another username"));
    }

    @Override
    public HttpStatus getHttpStatus(){
        return HttpStatus.CONFLICT;
    }
}
