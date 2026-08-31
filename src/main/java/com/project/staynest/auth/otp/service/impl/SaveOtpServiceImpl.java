package com.project.staynest.auth.otp.service.impl;

import com.project.staynest.auth.persistence.cache.redis.mapper.otp.OtpCacheDataMapper;
import com.project.staynest.auth.otp.model.OtpMetadata;
import com.project.staynest.auth.otp.service.SaveOtpService;
import com.project.staynest.auth.otp.OtpGenerator.OtpGenerator;
import com.project.staynest.auth.otp.constants.OtpPolicyConstants;
import com.project.staynest.auth.persistence.cache.port.otp.OtpCachePort;
import com.project.staynest.auth.persistence.cache.redis.model.otp.OtpCacheData;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Service;

@Service
public class SaveOtpServiceImpl implements SaveOtpService {

    private static final int MAX_VERIFY_ATTEMPTS = OtpPolicyConstants.MAX_VERIFY_ATTEMPTS;
    private static final int MAX_RESEND_ATTEMPTS = OtpPolicyConstants.MAX_RESEND_ATTEMPTS;

    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final OtpGenerator otpGenerator;
    private final OtpCachePort otpCachePort;
    public SaveOtpServiceImpl(
            OtpGenerator otpGenerator,
            OtpCachePort otpCachePort
    ){
        this.otpGenerator = otpGenerator;
        this.otpCachePort = otpCachePort;
    }

    @Override
    public String saveOtp(OtpMetadata otpMetadata) {
        Validation.validate(otpMetadata, "otpMetadata", CLASS_NAME);

        String otp = this.otpGenerator.generateRandomOTP();

        String hashedOtp = this.otpGenerator.generateOtpHash(otp);

        OtpCacheData otpData = OtpCacheDataMapper.from(
                otpMetadata,
                hashedOtp,
                MAX_VERIFY_ATTEMPTS,
                MAX_RESEND_ATTEMPTS
        );

        this.otpCachePort.saveOtp(
                otpData
        );

        return otp;

    }
}
