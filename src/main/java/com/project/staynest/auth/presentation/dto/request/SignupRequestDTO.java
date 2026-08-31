package com.project.staynest.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public class SignupRequestDTO {


    @NotBlank(message = "Username is required")
    @Size(min = 1, message = "Username too short")
    @Size(max = 100, message = "Username too long")
    @Pattern(
            regexp= "^(?=.*[a-z])(?![.-])[a-z0-9_.-]*[a-z0-9_]$"
    )
    private String username;

    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Email(message = "Invalid identifier format")
    private String email;

    public void setEmail(String email){
        this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
    public void setUsername(String username){
        this.username = username == null ? null : username.trim();
    }
    public String getEmail(){
        return this.email;
    }
    public String getUsername(){
        return this.username;
    }


}
