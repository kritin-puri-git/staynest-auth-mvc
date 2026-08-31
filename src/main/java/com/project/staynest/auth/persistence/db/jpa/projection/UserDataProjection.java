package com.project.staynest.auth.persistence.db.jpa.projection;

import com.project.staynest.auth.validation.Validation;


public record UserDataProjection(
        Long userLookupId,
        byte[] encryptedPublicId,
        byte[] encryptedUsername,
        byte[] encryptedEmail,
        short encryptionKeyId,
        short encryptionVersion,
        short hashingKeyId,
        short hashingVersion,
        String status
) {
    public UserDataProjection{
        String className = this.getClass().getSimpleName();
        Validation.validate(userLookupId, "userLookupId", className);
        Validation.validate(encryptedPublicId, "encryptedPublicId", className);
        Validation.validate(encryptedUsername, "encryptedUsername", className);
        Validation.validate(encryptedEmail, "encryptedEmail", className);

        if (encryptionKeyId <= 0) {
            throw new IllegalArgumentException(
                    "hashingKeyId cannot be negative in UserDataProjection"
            );
        }
        if (encryptionVersion <= 0) {
            throw new IllegalArgumentException(
                    "hashingVersion cannot be negative in UserDataProjection"
            );
        }
        if (hashingKeyId <= 0) {
            throw new IllegalArgumentException(
                    "hashingKeyId cannot be negative in UserDataProjection"
            );
        }
        if (hashingVersion <= 0) {
            throw new IllegalArgumentException(
                    "hashingVersion cannot be negative in UserDataProjection"
            );
        }

        encryptedPublicId = encryptedPublicId.clone();
        encryptedEmail = encryptedEmail.clone();
        encryptedUsername = encryptedUsername.clone();
    }

    @Override
    public byte[] encryptedPublicId(){
        return this.encryptedPublicId.clone();
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