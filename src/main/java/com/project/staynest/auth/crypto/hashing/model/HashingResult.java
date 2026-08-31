package com.project.staynest.auth.crypto.hashing.model;

import com.project.staynest.auth.validation.Validation;

public record HashingResult(
        byte[] hash,
        short keyId,
        short version
) {
    public HashingResult{
        Validation.validate(hash, "hash", this.getClass().getSimpleName());
        hash = hash.clone();
    }

    @Override
    public byte[] hash(){
        return this.hash.clone();
    }
}
