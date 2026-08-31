package com.project.staynest.auth.crypto.hashing.service.impl;

import com.project.staynest.auth.crypto.hashing.algorithm.HashingAlgorithm;
import com.project.staynest.auth.crypto.hashing.algorithm.registry.HashingVersionRegistry;
import com.project.staynest.auth.crypto.hashing.model.HashingData;
import com.project.staynest.auth.crypto.hashing.model.HashingResult;
import com.project.staynest.auth.crypto.hashing.model.HashingResultMap;
import com.project.staynest.auth.crypto.hashing.service.HashingService;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HashingServiceImpl implements HashingService {


    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final short activeVersion;
    private final Map<Short, HashingAlgorithm> hashingAlgorithms;
    public HashingServiceImpl(
            HashingVersionRegistry hashingVersionRegistry
    ){
        this.activeVersion = hashingVersionRegistry.getActiveVersion();
        this.hashingAlgorithms = Map.copyOf(hashingVersionRegistry.getHashingAlgorithmMap());
    }

    @Override
    public boolean isLatest(final short keyId, final short version) {
        final short activeVersion = this.activeVersion;
        final HashingAlgorithm activeHashingAlgorithm = this.hashingAlgorithms.get(activeVersion);
        return keyId == activeHashingAlgorithm.getActiveKeyId()
                && version == activeVersion;
    }

    @Override
    public HashingResult hash(final String plainData) {
        Validation.validate(plainData, "plainData", CLASS_NAME);

        final short activeVersion = this.activeVersion;
        final HashingAlgorithm hashingAlgorithm = this.hashingAlgorithms.get(activeVersion);

        HashingData hashingData = hashingAlgorithm.hash(
                plainData
        );

        return new HashingResult(
                hashingData.hashingData(),
                hashingData.keyId(),
                activeVersion
        );
    }

    @Override
    public HashingResultMap hash(Map<String, String> plainDataMap) {
        Validation.validate(plainDataMap, "plainData", CLASS_NAME);

        final short activeVersion = this.activeVersion;
        final HashingAlgorithm hashingAlgorithm = this.hashingAlgorithms.get(activeVersion);

        final short activeKeyId = hashingAlgorithm.getActiveKeyId();
        final Map<String, byte[]> hashDataMap = new HashMap<>(plainDataMap.size());
        for(Map.Entry<String, String> plainDataMapEntry : plainDataMap.entrySet()){
            hashDataMap.put(
                    plainDataMapEntry.getKey(),
                    hashingAlgorithm.hash(
                            plainDataMapEntry.getValue(),
                            activeKeyId
                    )
            );
        }
        return new HashingResultMap(
                hashDataMap,
                activeKeyId,
                activeVersion
        );
    }

    @Override
    public List<HashingResult> detailedHashCandidates(final String data) {
        Validation.validate(data, "data", CLASS_NAME);

        final Map<Short, HashingAlgorithm> hashingAlgorithms = this.hashingAlgorithms;

        final List<HashingResult> hashingResultList = new ArrayList<>();
        for(Map.Entry<Short, HashingAlgorithm> hashingAlgorithmsEntry : hashingAlgorithms.entrySet()){

            final short hashingVersion = hashingAlgorithmsEntry.getKey();
            final HashingAlgorithm hashingAlgorithm = hashingAlgorithmsEntry.getValue();
            final List<HashingData> hashingDataList = hashingAlgorithm.getHashCandidates(data);

            for(HashingData hashingData : hashingDataList){
                hashingResultList.add(
                        new HashingResult(
                                hashingData.hashingData(),
                                hashingData.keyId(),
                                hashingVersion
                        )
                );
            }
        }

        return hashingResultList;
    }

}
