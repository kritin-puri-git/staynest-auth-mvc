package com.project.staynest.auth.crypto.hashing.model;

import com.project.staynest.auth.validation.Validation;

import java.util.Map;

public record HashingResultMap(
        Map<String, byte[]> hashDataMap,
        short keyId,
        short version
) {
    public HashingResultMap {
        Validation.validate(hashDataMap, "hashDataMap", this.getClass().getSimpleName());

        hashDataMap = Map.copyOf(hashDataMap);
    }

    @Override
    public Map<String, byte[]> hashDataMap(){
        return Map.copyOf(this.hashDataMap);
    }
}
