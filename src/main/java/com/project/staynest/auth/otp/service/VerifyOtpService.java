package com.project.staynest.auth.otp.service;

import com.project.staynest.auth.otp.model.VerifyOtpDetails;

public interface VerifyOtpService {
    void verifyOtp(VerifyOtpDetails verifyOtpDetails);
}
