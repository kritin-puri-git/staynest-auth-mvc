package com.project.staynest.auth.otp.mapper;

import com.project.staynest.auth.otp.model.EmailOtpData;
import com.project.staynest.auth.otp.model.OtpMetadata;

public final class OtpMetaDataMapper {
    private OtpMetaDataMapper(){}

    public static OtpMetadata from(EmailOtpData emailOtpData){
        return new OtpMetadata(
                emailOtpData.purpose(),
                emailOtpData.email(),
                emailOtpData.token(),
                emailOtpData.expiry(),
                emailOtpData.ttl()
        );
    }
}
