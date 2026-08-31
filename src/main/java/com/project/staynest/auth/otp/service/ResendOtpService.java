package com.project.staynest.auth.otp.service;

import com.project.staynest.auth.otp.model.OtpMetadata;

public interface ResendOtpService {
    String resendOtp(OtpMetadata otpData);
}
