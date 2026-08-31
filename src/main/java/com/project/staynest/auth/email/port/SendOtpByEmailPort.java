package com.project.staynest.auth.email.port;

import com.project.staynest.auth.email.models.SendOtpByEmailEvent;

public interface SendOtpByEmailPort {

    void sendOtpByEmail(SendOtpByEmailEvent otpEvent);
}
