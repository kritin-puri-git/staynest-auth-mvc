package com.project.staynest.auth.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.staynest.auth.errorhandling.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ErrorResponseDTO {

    private final ErrorCode errorCode;
    private final String errorId;
    private final String message;
    private final Object details;

    private ErrorResponseDTO(ErrorCode code, String message, Object details) {
        this.errorCode = code;
        this.errorId = code.getId();
        this.message = message;
        this.details = details;
    }

    public static ErrorResponseDTO of(ErrorCode code, String message,  Object details) {
        return  new ErrorResponseDTO(code, message, details);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
    public String getErrorId() {
        return errorId;
    }
    public String getMessage() {
        return message;
    }
    public Object getDetails() {
        return details;
    }
}