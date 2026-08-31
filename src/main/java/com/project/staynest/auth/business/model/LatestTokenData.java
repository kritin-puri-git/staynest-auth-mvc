package com.project.staynest.auth.business.model;

import com.project.staynest.auth.business.model.jwt.JwtGenerationResult;
import com.project.staynest.auth.validation.Validation;

public record LatestTokenData(
        JwtGenerationResult jwtGenerationResult,
        String publicId
) {
    public LatestTokenData {
        String className = this.getClass().getSimpleName();
        Validation.validate(jwtGenerationResult, "jwtGenerationResult", className);
        Validation.validate(publicId, "publicId", className);
    }

}
