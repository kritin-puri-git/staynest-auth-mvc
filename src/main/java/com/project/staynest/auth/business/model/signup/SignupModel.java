package com.project.staynest.auth.business.model.signup;

import com.project.staynest.auth.validation.Validation;

public record SignupModel(
        String username,
        String email
) {

    public SignupModel{
        String className = this.getClass().getSimpleName();

        Validation.validate(username, "username", className);
        Validation.validate(email, "identifier", className);

        username = username.trim();
        email = email.trim();

    }
}
