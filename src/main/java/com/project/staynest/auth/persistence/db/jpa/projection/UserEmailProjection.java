package com.project.staynest.auth.persistence.db.jpa.projection;

import com.project.staynest.auth.validation.Validation;

public record UserEmailProjection(
        byte[] encryptedEmail,
        short encryptionKeyId,
        short encryptionVersion
) {

    public UserEmailProjection {
        String className = this.getClass().getSimpleName();
        Validation.validate(encryptedEmail, "encryptedEmail", className);

        if (encryptionKeyId <= 0) {
            throw new IllegalArgumentException(
                    "hashingKeyId cannot be negative in UserEmailProjection"
            );
        }
        if (encryptionVersion <= 0) {
            throw new IllegalArgumentException(
                    "hashingVersion cannot be negative in UserEmailProjection"
            );
        }

        encryptedEmail = encryptedEmail.clone();
    }


    @Override
    public byte[] encryptedEmail(){
        return this.encryptedEmail.clone();
    }

}