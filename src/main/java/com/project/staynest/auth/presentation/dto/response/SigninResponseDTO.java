package com.project.staynest.auth.presentation.dto.response;

import com.project.staynest.auth.validation.Validation;

public record SigninResponseDTO(
        String username,
        String email
) {
    public SigninResponseDTO {
        String className = this.getClass().getSimpleName();
        Validation.validate(username, "username", className);
        Validation.validate(email, "email", className);
    }

}
