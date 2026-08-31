package com.project.staynest.auth.otp.constants;

public  class OtpPolicyConstants {
    public static final int MIN_OTP_EXPIRY = 30;
    public static final int MIN_OTP_TTL = 40;
    public static final int MAX_VERIFY_ATTEMPTS = 3;
    public static final int MAX_RESEND_ATTEMPTS = 3;
    public static final int RESEND_COOLDOWN_SECONDS = 30;
}
