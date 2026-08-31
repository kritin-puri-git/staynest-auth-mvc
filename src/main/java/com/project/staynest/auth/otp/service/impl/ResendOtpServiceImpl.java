package com.project.staynest.auth.otp.service.impl;

import com.project.staynest.auth.otp.model.OtpMetadata;
import com.project.staynest.auth.persistence.cache.redis.mapper.otp.OtpCacheKeyDataMapper;
import com.project.staynest.auth.persistence.cache.redis.mapper.otp.OtpCacheMetaDataMapper;
import com.project.staynest.auth.persistence.cache.redis.mapper.otp.ResendOtpCacheDataMapper;

import com.project.staynest.auth.otp.service.ResendOtpService;
import com.project.staynest.auth.errorhandling.exceptions.otp.OtpExpiredException;
import com.project.staynest.auth.errorhandling.exceptions.otp.OtpResendAttemptsExceededException;
import com.project.staynest.auth.errorhandling.exceptions.otp.OtpResendCooldownActiveException;
import com.project.staynest.auth.errorhandling.exceptions.otp.OtpResendRateLimitedException;
import com.project.staynest.auth.otp.OtpGenerator.OtpGenerator;
import com.project.staynest.auth.otp.constants.OtpPolicyConstants;
import com.project.staynest.auth.otp.enums.ResendOtpStatus;
import com.project.staynest.auth.persistence.cache.port.otp.OtpCachePort;
import com.project.staynest.auth.persistence.cache.redis.model.otp.ResendOtpCacheData;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Service;

@Service
public class ResendOtpServiceImpl implements ResendOtpService {
    private static final int MAX_VERIFY_ATTEMPTS = OtpPolicyConstants.MAX_VERIFY_ATTEMPTS;
    private static final int RESEND_COOLDOWN_SECONDS = OtpPolicyConstants.RESEND_COOLDOWN_SECONDS;

    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final OtpGenerator otpGenerator;
    private final OtpCachePort otpCachePort;

    public ResendOtpServiceImpl(
            OtpGenerator otpGenerator,
            OtpCachePort otpCachePort
    ){
        this.otpGenerator = otpGenerator;
        this.otpCachePort = otpCachePort;
    }

    @Override
    public String resendOtp(OtpMetadata otpMetadata) {
        Validation.validate(otpMetadata, "otpMetadata", CLASS_NAME);

        boolean valid = this.otpCachePort.checkOtpValid(
                OtpCacheMetaDataMapper.from(otpMetadata)
        );
        if(!valid){
            throw new OtpExpiredException();
        }

        if(!this.otpCachePort.rateLimitResend(
                OtpCacheKeyDataMapper.from(
                        otpMetadata
                )
        )){
            throw new OtpResendRateLimitedException();
        }

        String otp = this.otpGenerator.generateRandomOTP();

        String hashedOtp = this.otpGenerator.generateOtpHash(otp);

        ResendOtpCacheData resendOtpData = ResendOtpCacheDataMapper.from(
                otpMetadata,
                hashedOtp,
                MAX_VERIFY_ATTEMPTS,
                RESEND_COOLDOWN_SECONDS
                );
        ResendOtpStatus result = this.otpCachePort.saveOtpAgain(
                resendOtpData
        );
        switch(result){
            case OTP_EXPIRED -> throw new OtpExpiredException();
            case COOLDOWN_ACTIVE -> throw new OtpResendCooldownActiveException();
            case ATTEMPTS_EXHAUSTED -> throw new OtpResendAttemptsExceededException();
        }
        return otp;
    }
}
