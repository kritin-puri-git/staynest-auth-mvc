package com.project.staynest.auth.crypto.hashing.algorithm;

import com.project.staynest.auth.crypto.hashing.model.HashingData;

import java.util.List;

public interface HashingAlgorithm {

    short getVersion();
    short getActiveKeyId();
    HashingData hash(String data);
    byte[] hash(String data, short keyId);
    List<HashingData> getHashCandidates(final String data);
}
