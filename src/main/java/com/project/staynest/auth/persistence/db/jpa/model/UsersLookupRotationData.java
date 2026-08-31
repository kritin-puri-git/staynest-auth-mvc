package com.project.staynest.auth.persistence.db.jpa.model;

import com.project.staynest.auth.validation.Validation;

public record UsersLookupRotationData(
        byte[] hashedPublicId,
        byte[] hashedUsername,
        byte[] hashedEmail,
        short hashingKeyId,
        short hashingVersion
) {
    public UsersLookupRotationData {
        String className = this.getClass().getSimpleName();
        Validation.validate(hashedPublicId, "hashedPublicId", className);
        Validation.validate(hashedUsername, "hashedUsername", className);
        Validation.validate(hashedEmail, "hashedEmail", className);

        if (hashingKeyId <= 0) {
            throw new IllegalArgumentException(
                    "hashingKeyId must be greater than 0 in UsersLookupRotationData"
            );
        }
        if (hashingVersion <= 0) {
            throw new IllegalArgumentException(
                    "hashingVersion must be greater than 0 in UsersLookupRotationData"
            );
        }

        hashedPublicId = hashedPublicId.clone();
        hashedEmail = hashedEmail.clone();
        hashedUsername = hashedUsername.clone();
    }

    @Override
    public byte[] hashedPublicId(){
        return this.hashedPublicId.clone();
    }
    @Override
    public byte[] hashedUsername(){
        return this.hashedUsername.clone();
    }
    @Override
    public byte[] hashedEmail(){
        return this.hashedEmail.clone();
    }

}