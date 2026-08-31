package com.project.staynest.auth.persistence.cache.redis.mapper.otp;

import com.project.staynest.auth.otp.model.OtpMetadata;
import com.project.staynest.auth.otp.model.VerifyOtpDetails;
import com.project.staynest.auth.persistence.cache.redis.model.otp.OtpCacheKeyData;

public final class OtpCacheKeyDataMapper {
    private OtpCacheKeyDataMapper(){

    }

    public static OtpCacheKeyData from(OtpMetadata otpMetadata){

        return new OtpCacheKeyData(
                otpMetadata.purpose(),
                otpMetadata.identifier(),
                otpMetadata.token()
        );
    }

    public static OtpCacheKeyData from(VerifyOtpDetails verifyOtpDetails){
        return new OtpCacheKeyData(
                verifyOtpDetails.purpose(),
                verifyOtpDetails.identifier(),
                verifyOtpDetails.token()
        );
    }

}
