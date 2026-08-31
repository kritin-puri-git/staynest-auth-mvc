package com.project.staynest.auth.crypto.hashing.service;

import com.project.staynest.auth.crypto.hashing.model.HashingResult;
import com.project.staynest.auth.crypto.hashing.model.HashingResultMap;

import java.util.List;
import java.util.Map;

public interface HashingService {
    boolean isLatest(short keyId, short version);
    HashingResult hash(String plainData);
    HashingResultMap hash(Map<String, String> plainDataMap);
    List<HashingResult> detailedHashCandidates(String data);
}
