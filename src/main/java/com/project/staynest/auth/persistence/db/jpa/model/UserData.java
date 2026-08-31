package com.project.staynest.auth.persistence.db.jpa.model;

import com.project.staynest.auth.validation.Validation;

public record UserData(
        byte[] encryptedPublicId,
        byte[] encryptedUsername,
        byte[] encryptedEmail,
        short encryptionKeyId,
        short encryptionVersion,
        byte[] publicIdHash,
        byte[] usernameHash,
        byte[] emailHash,
        short hashingKeyId,
        short hashingVersion,
        String status
) {
    public UserData {
        String className = this.getClass().getSimpleName();
        Validation.validate(encryptedPublicId, "encryptedPublicId", className);
        Validation.validate(encryptedUsername, "encryptedUsername", className);
        Validation.validate(encryptedEmail, "encryptedEmail", className);
        if (encryptionKeyId <= 0) {
            throw new IllegalArgumentException(
                    "encryptionKeyId must be positive in UserData"
            );
        }
        if (encryptionVersion <= 0) {
            throw new IllegalArgumentException(
                    "encryptionKeyId must be positive in UserData"
            );
        }

        Validation.validate(publicIdHash, "publicIdHash", className);
        Validation.validate(usernameHash, "usernameHash", className);
        Validation.validate(emailHash, "emailHash", className);
        if (hashingKeyId <= 0) {
            throw new IllegalArgumentException(
                    "hashingKeyId must be positive in UserData"
            );
        }
        if (hashingVersion <= 0) {
            throw new IllegalArgumentException(
                    "hashingVersion must be positive in UserData"
            );
        }

        Validation.validate(status, "status", className);

        encryptedPublicId = encryptedPublicId.clone();
        encryptedUsername = encryptedUsername.clone();
        encryptedEmail = encryptedEmail.clone();
        publicIdHash = publicIdHash.clone();
        usernameHash = usernameHash.clone();
        emailHash = emailHash.clone();
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

    @Override
    public byte[] publicIdHash(){
        return this.publicIdHash.clone();
    }

    @Override
    public byte[] usernameHash(){
        return this.usernameHash.clone();
    }

    @Override
    public byte[] emailHash(){
        return this.emailHash.clone();
    }


}
