package com.project.staynest.auth.crypto.encryption.model;

import com.project.staynest.auth.validation.Validation;

public record EncryptionData(
        byte[] encryptedData,
        short keyId
) {
    public EncryptionData{
        Validation.validate(encryptedData, "encryptedData", this.getClass().getSimpleName());
        encryptedData = encryptedData.clone();
    }

    @Override
    public byte[] encryptedData(){
        return this.encryptedData.clone();
    }
}
