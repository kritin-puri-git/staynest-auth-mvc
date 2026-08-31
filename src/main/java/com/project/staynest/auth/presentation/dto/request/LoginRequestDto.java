package com.project.staynest.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public class LoginRequestDto {

    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Email(message = "Invalid identifier format")
    private String email;

    public void setEmail(String email){
        this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
    public String getEmail(){
        return this.email;
    }

}
