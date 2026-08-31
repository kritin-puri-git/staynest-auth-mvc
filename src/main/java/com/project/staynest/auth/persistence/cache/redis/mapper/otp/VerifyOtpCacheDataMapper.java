package com.project.staynest.auth.persistence.cache.redis.mapper.otp;

import com.project.staynest.auth.otp.model.VerifyOtpDetails;
import com.project.staynest.auth.persistence.cache.redis.model.otp.VerifyOtpCacheData;

public final class VerifyOtpCacheDataMapper {
    private VerifyOtpCacheDataMapper(){}

    public static VerifyOtpCacheData from(
            VerifyOtpDetails verifyOtpDetails,
            String hashedOtp
    ){
        return new VerifyOtpCacheData(
                verifyOtpDetails.purpose(),
                verifyOtpDetails.identifier(),
                verifyOtpDetails.token(),
                hashedOtp
        );
    }
}
