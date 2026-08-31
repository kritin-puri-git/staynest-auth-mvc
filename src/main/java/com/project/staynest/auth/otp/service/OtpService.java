package com.project.staynest.auth.otp.service;

import com.project.staynest.auth.otp.model.OtpMetadata;
import com.project.staynest.auth.otp.model.VerifyOtpDetails;

public interface OtpService {
    String saveOtp(OtpMetadata otpMetadata);
    void verifyOtp(VerifyOtpDetails verifyOtpDetails);
    String resendOtp(OtpMetadata otpMetadata);
}
