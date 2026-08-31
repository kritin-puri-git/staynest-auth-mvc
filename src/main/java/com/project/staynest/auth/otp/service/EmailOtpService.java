package com.project.staynest.auth.otp.service;

import com.project.staynest.auth.otp.model.EmailOtpData;

public interface EmailOtpService {
    void send(EmailOtpData emailOtpData);
    void resend(EmailOtpData emailOtpData);
}
