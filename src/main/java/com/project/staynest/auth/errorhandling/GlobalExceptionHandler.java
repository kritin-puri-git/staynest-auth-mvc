package com.project.staynest.auth.errorhandling;

import com.project.staynest.auth.errorhandling.exceptions.base.BaseIllegalStateException;
import com.project.staynest.auth.errorhandling.exceptions.base.BaseRuntimeException;
import com.project.staynest.auth.errorhandling.exceptions.business.validation.EmailAlreadyExistsException;
import com.project.staynest.auth.errorhandling.exceptions.business.validation.InvalidAuthFieldsException;
import com.project.staynest.auth.presentation.dto.response.ErrorResponseDTO;
import com.project.staynest.auth.errorhandling.exceptions.internal.InvalidGenderEnumException;
import com.project.staynest.auth.presentation.dto.response.wrapper.ApiResponseDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return handleBaseRuntimeException(new InvalidAuthFieldsException(errors));

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex
    ){

        Throwable cause = ex;

        while (cause != null){
            if (cause instanceof InvalidGenderEnumException genderEx){

                return handleBaseRuntimeException(
                        new InvalidAuthFieldsException(genderEx.getErrorDetails())
                );
            }
            cause = cause.getCause();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseDTO.error(
                        "Authentication Error",
                        ErrorResponseDTO.of(ErrorCode.VALIDATION_FAILED,
                                "Invalid request payload", null)
                )
        );

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex
    ){

        String errorMessage = ex.getMostSpecificCause().getMessage();

        if(errorMessage.contains("Duplicate entry") && errorMessage.contains("users.email")){

            return handleBaseRuntimeException(new EmailAlreadyExistsException());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponseDTO.error(
                        "Authentication Error",
                        ErrorResponseDTO.of(ErrorCode.UNEXPECTED_ERROR,
                                "Unexpected Error Occurred", null)
                )
        );

    }

    @ExceptionHandler(BaseRuntimeException.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleBaseRuntimeException(
            BaseRuntimeException ex
    ){

        return ResponseEntity.status(ex.getHttpStatus()).body(
                ApiResponseDTO.error(
                        "Authentication Error",
                        ErrorResponseDTO.of(ex.getErrorCode(),
                                ex.getClientMessage(), ex.getErrorDetails())
                )
        );

    }

    @ExceptionHandler(BaseIllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleBaseIllegalStateException(
            BaseIllegalStateException ex
    ){

        System.out.println("BASE:" + ex);
        return ResponseEntity.status(ex.getHttpStatus()).body(
                ApiResponseDTO.error(
                        "Authentication Error",
                        ErrorResponseDTO.of(ex.getErrorCode(),
                                ex.getClientMessage(), null)
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleGlobalException(Exception ex){
        System.out.println("EXC: " + ex);
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseDTO.error(
                        "Unexpected Authentication Error",
                        ErrorResponseDTO.of(ErrorCode.UNEXPECTED_ERROR,
                                "Something went wrong", null)
                )
        );

    }
}