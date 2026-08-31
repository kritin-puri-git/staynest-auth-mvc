package com.project.staynest.auth.business.mapper.login;

import com.project.staynest.auth.business.model.login.VerifyLoginByEmailModel;
import com.project.staynest.auth.presentation.dto.request.VerifyEmailOtpRequestDto;

public final class VerifyLoginByEmailModelMapper {
    private VerifyLoginByEmailModelMapper(){}

    public static VerifyLoginByEmailModel from(
            VerifyEmailOtpRequestDto verifyDto,
            String deviceId,
            String userAgent
    ){
        return new VerifyLoginByEmailModel(
                verifyDto.getEmail(),
                verifyDto.getToken(),
                verifyDto.getOtp(),
                deviceId,
                userAgent
        );
    }
}