package com.project.staynest.auth.errorhandling;

import java.util.HashSet;
import java.util.Set;

public enum ErrorCode {

    VALIDATION_FAILED("VAL-001"),
    VALIDATION_FIELD_FAILED("VAL-002"),
    EMAIL_ALREADY_EXISTS("VAL-003"),
    TOO_MANY_SIGNUP_ATTEMPTS("VAL-004"),



    OTP_EXPIRED("AUTH-001"),
    OTP_NOT_MATCHED("AUTH-002"),
    TOO_MANY_OTP_VERIFY_ATTEMPTS("AUTH-003"),
    TOO_MANY_OTP_RESEND_ATTEMPTS("AUTH-004"),
    RESEND_COOLDOWN_ACTIVE("AUTH-005"),
    OTP_VERIFY_RATE_LIMITED("AUTH_006"),
    OTP_RESEND_RATE_LIMITED("AUTH_007"),
    SIGNUP_CACHE_EXPIRED("AUTH-008"),
    USER_NOT_FOUND("AUTH-009"),
    USER_NOT_FOUND_AFTER_OTP_VERIFIED("AUTH-010"),

    TOO_MANY_ATTEMPTS("ERR-001"),

    UNEXPECTED_ERROR("UNEX-001"),

    ;

    private final String id;

    private ErrorCode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    static{
        Set<String> errorId = new HashSet<>();
        for(ErrorCode errorCode : ErrorCode.values()) {
            if(!errorId.add(errorCode.getId())){
                throw new IllegalStateException("Duplicate error code " + errorCode);
            }
        }
    }

}