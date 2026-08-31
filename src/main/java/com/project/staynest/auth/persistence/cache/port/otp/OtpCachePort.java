package com.project.staynest.auth.persistence.cache.port.otp;

import com.project.staynest.auth.otp.enums.ResendOtpStatus;
import com.project.staynest.auth.otp.enums.VerifyOtpStatus;
import com.project.staynest.auth.persistence.cache.redis.model.otp.*;
import com.project.staynest.auth.persistence.cache.redis.model.otp.*;

public interface OtpCachePort {

    void saveOtp(OtpCacheData otpData);

    boolean checkOtpValid(OtpCacheMetadata cacheDetails);

    ResendOtpStatus saveOtpAgain(ResendOtpCacheData resendOtpData);

    VerifyOtpStatus verifyOtp(VerifyOtpCacheData verifyOtpData);

    boolean rateLimitVerify(OtpCacheKeyData cacheKeyData);
    boolean rateLimitResend(OtpCacheKeyData cacheKeyData);
}
