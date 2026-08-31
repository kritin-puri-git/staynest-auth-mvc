package com.project.staynest.auth.presentation.dto.response;

import com.project.staynest.auth.validation.Validation;

public record SendOtpResponseDTO(
        String identifier,
        String token
) {
    public SendOtpResponseDTO{
        String className = this.getClass().getSimpleName();
        Validation.validate(identifier, "identifier", className);
        Validation.validate(token, "token", className);
    }
}