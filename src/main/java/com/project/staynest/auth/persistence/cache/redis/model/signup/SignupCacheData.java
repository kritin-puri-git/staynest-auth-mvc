package com.project.staynest.auth.persistence.cache.redis.model.signup;

import com.project.staynest.auth.validation.Validation;

public record SignupCacheData(
        byte[] encryptedUsername,
        byte[] encryptedEmail,
        short encryptionKeyId,
        short encryptionVersion
) {
    public SignupCacheData {
        String className = this.getClass().getSimpleName();
        Validation.validate(encryptedUsername, "encryptedUsername", className);
        Validation.validate(encryptedEmail, "encryptedEmail", className);

        encryptedUsername = encryptedUsername.clone();
        encryptedEmail = encryptedEmail.clone();
    }
    @Override
    public byte[] encryptedUsername(){
        return this.encryptedUsername.clone();
    }
    @Override
    public byte[] encryptedEmail(){
        return this.encryptedEmail.clone();
    }

}
