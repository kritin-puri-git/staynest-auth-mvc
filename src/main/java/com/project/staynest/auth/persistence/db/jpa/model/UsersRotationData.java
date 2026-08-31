package com.project.staynest.auth.persistence.db.jpa.model;

import com.project.staynest.auth.validation.Validation;

public record UsersRotationData(
        byte[] encryptedPublicId,
        byte[] encryptedUsername,
        byte[] encryptedEmail,
        short encryptionKeyId,
        short encryptionVersion
) {
    public UsersRotationData {
        String className = this.getClass().getSimpleName();
        Validation.validate(encryptedPublicId, "encryptedPublicId", className);
        Validation.validate(encryptedUsername, "encryptedUsername", className);
        Validation.validate(encryptedEmail, "encryptedEmail", className);

        if (encryptionKeyId <= 0) {
            throw new IllegalArgumentException(
                    "encryptionKeyId must be greater than 0 in UsersRotationData"
            );
        }
        if (encryptionVersion <= 0) {
            throw new IllegalArgumentException(
                    "encryptionVersion must be greater than 0 in UsersRotationData"
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
    public byte[] encryptedEmail(){
        return this.encryptedEmail.clone();
    }
    @Override
    public byte[] encryptedUsername(){
        return this.encryptedUsername.clone();
    }

}