package com.project.staynest.auth.persistence.cache.redis.adapter;

import com.project.staynest.auth.errorhandling.exceptions.unexpected.UnexpectedIllegalStateException;
import com.project.staynest.auth.otp.enums.ResendOtpStatus;
import com.project.staynest.auth.otp.enums.VerifyOtpStatus;
import com.project.staynest.auth.persistence.cache.redis.model.otp.*;
import com.project.staynest.auth.persistence.cache.port.otp.OtpCachePort;
import com.project.staynest.auth.persistence.cache.redis.keys.RedisKeys;
import com.project.staynest.auth.persistence.cache.redis.model.otp.*;
import com.project.staynest.auth.validation.Validation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public final class RedisOtpCacheAdapter implements OtpCachePort {

    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Long> saveOtpScript;
    private final RedisScript<Long> checkOtpValidScript;
    private final RedisScript<Long> resendOtpScript;
    private final RedisScript<Long> verifyOtpScript;
    public RedisOtpCacheAdapter(
            @Qualifier("masterRedisTemplate")
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("saveOtpScript")
            RedisScript<Long> saveOtpScript,
            @Qualifier("checkOtpValidScript")
            RedisScript<Long> checkOtpValidScript,
            @Qualifier("resendOtpScript")
            RedisScript<Long> resendOtpScript,
            @Qualifier("verifyOtpScript")
            RedisScript<Long> verifyOtpScript
    ){
        this.redisTemplate = redisTemplate;
        this.saveOtpScript = saveOtpScript;
        this.checkOtpValidScript = checkOtpValidScript;
        this.resendOtpScript = resendOtpScript;
        this.verifyOtpScript = verifyOtpScript;
    }

    @Override
    public void saveOtp(OtpCacheData otpData) {
        Validation.validate(otpData, "otpData", CLASS_NAME);

        String key = RedisKeys.getOtpKey(
                otpData.purpose(),
                otpData.identifier(),
                otpData.token()

        );

        Long result = redisTemplate.execute(
                saveOtpScript,
                List.of(key),
                otpData.hashedOtp(),
                otpData.remainingVerifyAttempts(),
                otpData.remainingResendAttempts(),
                otpData.ttl()
        );

        if(result == null){
            throw new UnexpectedIllegalStateException(
                    "Redis Returned null while saving otp"
            );
        }

        if(result == 0L){
            throw new UnexpectedIllegalStateException(
                    "Couldn't save OTP in Cache. Same key already exists with same identifier and token(UUID)"
            );
        }
    }


    @Override
    public boolean checkOtpValid(OtpCacheMetadata cacheMetadata){
        Validation.validate(cacheMetadata, "cacheMetadata", CLASS_NAME);
        String key = RedisKeys.getOtpKey(
                cacheMetadata.purpose(),
                cacheMetadata.identifier(),
                cacheMetadata.token()
        );

        Long result = redisTemplate.execute(
                checkOtpValidScript,
                List.of(key),
                cacheMetadata.expiry()
        );

        if(result == null){
            throw new UnexpectedIllegalStateException(
                    "Redis Returned null while checking otp valid or not"
            );
        }

        return result == 1L;

    }


    @Override
    public VerifyOtpStatus verifyOtp(VerifyOtpCacheData verifyOtpData){
        Validation.validate(verifyOtpData, "verifyOtpData", CLASS_NAME);
        String key = RedisKeys.getOtpKey(
                verifyOtpData.purpose(),
                verifyOtpData.identifier(),
                verifyOtpData.token()
        );
        Long result = redisTemplate.execute(
                verifyOtpScript,
                List.of(key),
                verifyOtpData.hashedOtp()
        );


        if(result == null){
            throw new UnexpectedIllegalStateException(
                    "Redis Returned null while verifying OTP"
            );
        }

        return switch(result.intValue()){

            case -2 -> VerifyOtpStatus.OTP_EXPIRED;


            case -3 -> VerifyOtpStatus.ATTEMPTS_EXHAUSTED;


            case -4 -> throw new UnexpectedIllegalStateException(
                    "Corrupted/Incomplete State. OTP key existed but fields missing. "
            );

            case -5 -> VerifyOtpStatus.OTP_NOT_MATCHED;

            case 1 -> VerifyOtpStatus.SUCCESS;

            default -> throw new UnexpectedIllegalStateException(
                    "Unknown result from verify-otp.lua result: " + result
            );


        };
    }

    @Override
    public ResendOtpStatus saveOtpAgain(ResendOtpCacheData resendOtpData) {
        Validation.validate(resendOtpData, "resendOtpData", CLASS_NAME);
        String key = RedisKeys.getOtpKey(
                resendOtpData.purpose(),
                resendOtpData.identifier(),
                resendOtpData.token()
        );

        Long result = redisTemplate.execute(
                resendOtpScript,
                List.of(key),
                resendOtpData.resendCooldown(),
                resendOtpData.hashedOtp(),
                resendOtpData.maxVerifyAttempts(),
                resendOtpData.ttl()
        );


        if(result == null){
            throw new UnexpectedIllegalStateException(
                    "Redis Returned null while resending OTP"
            );
        }

        return switch(result.intValue()){
            case 0 -> throw new UnexpectedIllegalStateException(
                        "Corrupted/Incomplete State. OTP key existed but fields missing. "
                );


            case -1 -> ResendOtpStatus.OTP_EXPIRED;


            case -2 -> ResendOtpStatus.COOLDOWN_ACTIVE;


            case -3 -> ResendOtpStatus.ATTEMPTS_EXHAUSTED;


            case 1 -> ResendOtpStatus.SUCCESS;

            default -> throw new UnexpectedIllegalStateException(
                        "Unknown result from resend-otp.lua result: " + result
                );


        };


    }


    @Override
    public boolean rateLimitVerify(OtpCacheKeyData cacheKeyData){
        Validation.validate(cacheKeyData, "cacheKeyData", CLASS_NAME);
        String key = RedisKeys.getOtpVerifyRateLimitKey(
                cacheKeyData.purpose(),
                cacheKeyData.identifier(),
                cacheKeyData.token()
        );

        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(
                        key,
                        "locked",
                        Duration.ofSeconds(5)
                );

        if(result == null){
            throw new UnexpectedIllegalStateException(
                    "Redis Returned null while saving verification rate limit key"
            );
        }

        return result;
    }

    @Override
    public boolean rateLimitResend(OtpCacheKeyData cacheKeyData){
        Validation.validate(cacheKeyData, "cacheKeyData", CLASS_NAME);
        String key = RedisKeys.getOtpResendRateLimitKey(
                cacheKeyData.purpose(),
                cacheKeyData.identifier(),
                cacheKeyData.token()
        );

        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(
                        key,
                        "locked",
                        Duration.ofSeconds(5)
                );

        if(result == null){
            throw new UnexpectedIllegalStateException(
                    "Redis Returned null while saving verification rate limit key"
            );
        }

        return result;
    }

}
