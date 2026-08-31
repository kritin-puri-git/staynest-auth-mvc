package com.project.staynest.auth.business.mapper.signup;

import com.project.staynest.auth.business.model.signup.VerifySignupModel;
import com.project.staynest.auth.presentation.dto.request.VerifyEmailOtpRequestDto;

public final class VerifySignupModelMapper {
    private VerifySignupModelMapper(){}

    public static VerifySignupModel from(
            VerifyEmailOtpRequestDto verifyEmailOtpRequestDto,
            String deviceId,
            String userAgent
    ){
        return new VerifySignupModel(
                verifyEmailOtpRequestDto.getEmail(),
                verifyEmailOtpRequestDto.getToken(),
                verifyEmailOtpRequestDto.getOtp(),
                deviceId,
                userAgent
        );
    }
}
