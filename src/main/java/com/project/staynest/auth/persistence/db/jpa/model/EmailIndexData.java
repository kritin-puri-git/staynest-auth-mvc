package com.project.staynest.auth.persistence.db.jpa.model;

import com.project.staynest.auth.validation.Validation;

public record EmailIndexData(
        byte[] emailIndex,
        short keyId,
        short version

) {
    public EmailIndexData {
        Validation.validate(emailIndex, "emailIndex", this.getClass().getSimpleName());

        if (keyId <= 0) {
            throw new IllegalArgumentException(
                    "keyId cannot be negative in EmailIndexData"
            );
        }

        if (version <= 0) {
            throw new IllegalArgumentException(
                    "version cannot be negative in EmailIndexData"
            );
        }
        emailIndex = emailIndex.clone();
    }


    @Override
    public byte[] emailIndex() {
        return this.emailIndex.clone();
    }
}
