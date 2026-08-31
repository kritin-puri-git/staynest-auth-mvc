package com.project.staynest.auth.persistence.cache.redis.mapper.otp;

import com.project.staynest.auth.otp.model.OtpMetadata;
import com.project.staynest.auth.otp.model.VerifyOtpDetails;
import com.project.staynest.auth.persistence.cache.redis.model.otp.OtpCacheMetadata;

public final class OtpCacheMetaDataMapper {
    private OtpCacheMetaDataMapper(){}

    public static OtpCacheMetadata from(OtpMetadata otpMetadata){

        return new OtpCacheMetadata(
                otpMetadata.purpose(),
                otpMetadata.identifier(),
                otpMetadata.token(),
                otpMetadata.expiry()
        );
    }

    public static OtpCacheMetadata from(VerifyOtpDetails verifyOtpDetails){
        return new OtpCacheMetadata(
                verifyOtpDetails.purpose(),
                verifyOtpDetails.identifier(),
                verifyOtpDetails.token(),
                verifyOtpDetails.expiry()
        );
    }

}
