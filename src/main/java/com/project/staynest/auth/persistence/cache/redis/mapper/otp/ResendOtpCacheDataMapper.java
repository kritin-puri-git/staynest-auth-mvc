package com.project.staynest.auth.persistence.cache.redis.mapper.otp;

import com.project.staynest.auth.otp.model.OtpMetadata;
import com.project.staynest.auth.persistence.cache.redis.model.otp.ResendOtpCacheData;

public final class ResendOtpCacheDataMapper {
    private ResendOtpCacheDataMapper(){

    }

    public static ResendOtpCacheData from(
            OtpMetadata otpMetadata,
            String hashedOtp,
            int maxVerifyAttempts,
            int resendCooldown
    ){

        return new ResendOtpCacheData(
                otpMetadata.purpose(),
                otpMetadata.identifier(),
                otpMetadata.token(),
                hashedOtp,
                maxVerifyAttempts,
                resendCooldown,
                otpMetadata.ttl()

        );
    }

}
