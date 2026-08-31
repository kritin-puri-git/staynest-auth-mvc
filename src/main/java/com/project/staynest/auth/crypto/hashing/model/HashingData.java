package com.project.staynest.auth.crypto.hashing.model;

import com.project.staynest.auth.validation.Validation;

public record HashingData(
        byte[] hashingData,
        short keyId
) {
    public HashingData{
        Validation.validate(hashingData, "hashingData", this.getClass().getSimpleName());
        hashingData = hashingData.clone();
    }

    @Override
    public byte[] hashingData(){
        return this.hashingData.clone();
    }
}
