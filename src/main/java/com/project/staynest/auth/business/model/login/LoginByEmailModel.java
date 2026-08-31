package com.project.staynest.auth.business.model.login;


import com.project.staynest.auth.validation.Validation;

public record LoginByEmailModel(
        String email
) {

    public LoginByEmailModel {
        Validation.validate(email, "identifier", this.getClass().getSimpleName());
        email = email.trim();
    }
}
