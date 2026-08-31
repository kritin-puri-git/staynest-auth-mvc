package com.project.staynest.auth.business.model;

import com.project.staynest.auth.business.model.jwt.JwtGenerationResult;
import com.project.staynest.auth.validation.Validation;

public record SigninData(
        JwtGenerationResult jwtGenerationResult,
        String publicId,
        String username,
        String email
) {
    public SigninData {
        String className = this.getClass().getSimpleName();
        Validation.validate(jwtGenerationResult, "jwtGenerationResult", className);
        Validation.validate(publicId, "publicId", className);
        Validation.validate(username, "username", className);
        Validation.validate(email, "email", className);
    }

}
