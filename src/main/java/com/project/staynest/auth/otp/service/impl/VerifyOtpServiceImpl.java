package com.project.staynest.auth.otp.service.impl;

import com.project.staynest.auth.persistence.cache.redis.mapper.otp.OtpCacheKeyDataMapper;
import com.project.staynest.auth.persistence.cache.redis.mapper.otp.OtpCacheMetaDataMapper;
import com.project.staynest.auth.persistence.cache.redis.mapper.otp.VerifyOtpCacheDataMapper;
import com.project.staynest.auth.otp.model.VerifyOtpDetails;
import com.project.staynest.auth.otp.service.VerifyOtpService;
import com.project.staynest.auth.errorhandling.exceptions.otp.OtpExpiredException;
import com.project.staynest.auth.errorhandling.exceptions.otp.OtpNotMatchedException;
import com.project.staynest.auth.errorhandling.exceptions.otp.OtpVerifyAttemptsExceededException;
import com.project.staynest.auth.errorhandling.exceptions.otp.OtpVerifyRateLimitedException;
import com.project.staynest.auth.otp.OtpGenerator.OtpGenerator;
import com.project.staynest.auth.otp.enums.VerifyOtpStatus;
import com.project.staynest.auth.persistence.cache.port.otp.OtpCachePort;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Service;

@Service
public class VerifyOtpServiceImpl implements VerifyOtpService {

    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final OtpCachePort otpCachePort;
    private final OtpGenerator otpGenerator;
    public VerifyOtpServiceImpl(
            OtpCachePort otpCachePort,
            OtpGenerator otpGenerator
            ){
        this.otpCachePort = otpCachePort;
        this.otpGenerator = otpGenerator;
    }

    @Override
    public void verifyOtp(VerifyOtpDetails verifyOtpDetails) {
        Validation.validate(verifyOtpDetails, "verifyOtpDetails", CLASS_NAME);


        boolean valid = this.otpCachePort.checkOtpValid(
                OtpCacheMetaDataMapper.from(verifyOtpDetails)
        );

        if(!valid){
            throw new OtpExpiredException();
        }

        if(!this.otpCachePort.rateLimitVerify(
                OtpCacheKeyDataMapper.from(
                        verifyOtpDetails
                )
        )){
            throw new OtpVerifyRateLimitedException();
        }

        String hashedOtp = this.otpGenerator.generateOtpHash(verifyOtpDetails.otp());


        VerifyOtpStatus verifyStatus = this.otpCachePort.verifyOtp(
                VerifyOtpCacheDataMapper.from(
                        verifyOtpDetails,
                        hashedOtp
                )
        );

        switch(verifyStatus){
            case OTP_EXPIRED -> throw new OtpExpiredException();
            case ATTEMPTS_EXHAUSTED -> throw new OtpVerifyAttemptsExceededException();
            case OTP_NOT_MATCHED -> throw new OtpNotMatchedException();
        }

    }
}
