package com.project.staynest.auth.persistence.cache.redis.mapper.otp;

import com.project.staynest.auth.otp.model.OtpMetadata;
import com.project.staynest.auth.persistence.cache.redis.model.otp.OtpCacheData;

public final class OtpCacheDataMapper {

    private OtpCacheDataMapper(){

    }

    public static OtpCacheData from(
            OtpMetadata otpMetadata,
            String hashedOtp,
            int remainingVerifyAttempts,
            int remainingResendAttempts
    ){

        return new OtpCacheData(
                otpMetadata.purpose(),
                otpMetadata.identifier(),
                otpMetadata.token(),
                hashedOtp,
                remainingVerifyAttempts,
                remainingResendAttempts,
                otpMetadata.ttl()
        );

    }

}
