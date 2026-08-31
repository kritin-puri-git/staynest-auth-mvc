package com.project.staynest.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VerifyEmailOtpRequestDto {

    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Session token is required")
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Invalid session token format"
    )
    private String token;

    @NotBlank(message = "OTP cannot be blank")
    @Size(min = 6, max = 6, message = "OTP must be of 6 digits")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "OTP must be of 6 digits only"
    )
    private String otp;

    public void setEmail(String email){
        this.email = email == null ? null : email.trim().toLowerCase();
    }

    public void setToken(String token){
        this.token = token == null ? null : token.trim();
    }

    public void setOtp(String otp){
        this.otp = otp == null ? null : otp.trim();
    }


    public String getEmail(){
        return this.email;
    }

    public String getToken(){
        return this.token;
    }

    public String getOtp(){
        return this.otp;
    }

}
