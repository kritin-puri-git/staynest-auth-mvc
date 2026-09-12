package com.project.staynest.auth.persistence.cache.redis.adapter;

import com.project.staynest.auth.errorhandling.exceptions.unexpected.UnexpectedIllegalStateException;
import com.project.staynest.auth.persistence.cache.port.auth.SignupCachePort;
import com.project.staynest.auth.persistence.cache.redis.keys.RedisKeys;
import com.project.staynest.auth.persistence.cache.redis.model.signup.SignupCacheKeyData;
import com.project.staynest.auth.persistence.cache.redis.model.signup.SignupCacheSessionData;
import com.project.staynest.auth.persistence.cache.redis.model.signup.SignupCacheData;
import com.project.staynest.auth.validation.Validation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;

@Component
public final class RedisSignupCacheAdapter implements SignupCachePort {

    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final RedisTemplate<String,Object> redisTemplate;
    private final RedisScript<Long> saveSignupCacheScript;
    public RedisSignupCacheAdapter(
            @Qualifier("masterRedisTemplate")
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("saveSignupCacheScript")
            RedisScript<Long> saveSignupCacheScript
            ){
        this.redisTemplate = redisTemplate;
        this.saveSignupCacheScript = saveSignupCacheScript;
    }


    @Override
    public void saveSignupCache(SignupCacheSessionData signupCacheData) {
        Validation.validate(signupCacheData, "signupCacheData", CLASS_NAME);

        String key = RedisKeys.getSignupKey(
                signupCacheData.hashedEmail(),
                signupCacheData.token()
        );

        Long result = redisTemplate.execute(
                saveSignupCacheScript,
                List.of(key),
                Base64.getEncoder().encodeToString(signupCacheData.encryptedUsername()),
                Base64.getEncoder().encodeToString(signupCacheData.encryptedEmail()),
                signupCacheData.encryptionKeyId(),
                signupCacheData.encryptionVersion(),
                signupCacheData.ttl()
        );

        if(result == null){
            throw new UnexpectedIllegalStateException(
                    "Redis Returned null while saving signup data"
            );
        }

        if(result == 1L){
            return;
        }

        switch(result.intValue()){
            case -1 ->throw new UnexpectedIllegalStateException(
                    "Couldn't save signup data in Cache. " +
                            "Same key already exists with same identifier " +
                            "and token(UUID)"
            );

            case 0 -> throw new UnexpectedIllegalStateException(
                    "Signup data provided to save found corrupted"
            );

            default -> throw new UnexpectedIllegalStateException(
                    "Unknown Lua result: " + result
            );
        }

    }

    @Override
    public SignupCacheData getSignupData(SignupCacheKeyData keyData) {
        Validation.validate(keyData, "keyData", CLASS_NAME);

        String key = RedisKeys.getSignupKey(keyData.identifier(), keyData.token());

        List<Object> signupCacheDataList = redisTemplate
                .opsForHash()
                .multiGet(
                        key,
                        List.of(
                                "encryptedUsername",
                                "encryptedEmail",
                                "encryptionKeyId",
                                "encryptionVersion"
                        )
                );

        if(signupCacheDataList == null){
            throw new UnexpectedIllegalStateException(
                    "Redis returned null while fetching signup cache data"
            );
        }

        if(signupCacheDataList.size() != 4){
            throw new UnexpectedIllegalStateException(
                    "Unexpected Redis response size while fetching signup cache data"
            );
        }

        if(signupCacheDataList.contains(null)){
            throw new UnexpectedIllegalStateException(
                    "Corrupted signup cache data found while fetching. Fields found null"
            );
        }

        Object encryptedUsernameObject = signupCacheDataList.get(0);
        Object encryptedEmailObject = signupCacheDataList.get(1);
        Object encryptionKeyIdObject = signupCacheDataList.get(2);
        Object encryptionVersionObject = signupCacheDataList.get(3);

        if (!(encryptedUsernameObject instanceof String encodedUsername) ||
                !(encryptedEmailObject instanceof String encodedEmail)) {

            throw new UnexpectedIllegalStateException(
                    "Corrupted signup cache data. Invalid data."
            );
        }

        byte[] encryptedUsername = Base64.getDecoder().decode(encodedUsername);
        byte[] encryptedEmail = Base64.getDecoder().decode(encodedEmail);


        if (!(encryptionKeyIdObject instanceof Number encryptionKeyIdNumber) ||
                !(encryptionVersionObject instanceof Number encryptionVersionNumber)) {
            throw new UnexpectedIllegalStateException(
                    "Corrupted signup cache data. Invalid numeric data type."
            );
        }

        short encryptionKeyId = encryptionKeyIdNumber.shortValue();
        short encryptionVersion = encryptionVersionNumber.shortValue();

        if(encryptedUsername.length == 0
                        || encryptedEmail.length == 0){
            throw new UnexpectedIllegalStateException(
                    "Corrupted signup cache data found while fetching. Length of encrypted data bytes is 0"
            );
        }
        if(encryptionKeyId <= 0
                || encryptionVersion <= 0){
            throw new UnexpectedIllegalStateException(
                    "Corrupted signup cache data found while fetching. " +
                            "EncryptionKeyId or EncryptionVersion must be greater than 0"
            );
        }

        return new SignupCacheData(
                encryptedUsername,
                encryptedEmail,
                encryptionKeyId,
                encryptionVersion
        );
    }
}