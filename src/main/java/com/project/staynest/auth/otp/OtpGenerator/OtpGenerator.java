package com.project.staynest.auth.otp.OtpGenerator;

import com.project.staynest.auth.crypto.hashing.service.HashingService;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

@Component
public final class OtpGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    String CLASS_NAME = this.getClass().getSimpleName();

    private final HashingService hashingService;
    public OtpGenerator(
            HashingService hashingService
    ){
        this.hashingService = hashingService;
    }


    public String generateRandomOTP(){
        return String.format("%06d", SECURE_RANDOM.nextInt(1000000));
    }

    public String generateOtpHash(String otp){

        Validation.validate(otp, "otp", CLASS_NAME);

        byte[] hashBytes = this.hashingService.hash(
                otp
        ).hash();
        Validation.validate(hashBytes, "valueHash", CLASS_NAME);
        return HexFormat.of().formatHex(hashBytes);
    }

    public boolean compareOtpHash(String otp, String hashedOtp){
        Validation.validate(otp, "otp", CLASS_NAME);
        Validation.validate(hashedOtp, "hashedOtp", CLASS_NAME);

        String generatedHash = generateOtpHash(otp);
        return MessageDigest.isEqual(
                generatedHash.getBytes(StandardCharsets.UTF_8),
                hashedOtp.getBytes(StandardCharsets.UTF_8)
        );
    }

}
