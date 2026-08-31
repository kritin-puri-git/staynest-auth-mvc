package com.project.staynest.auth.business.facade;

import com.project.staynest.auth.crypto.encryption.model.EncryptionResult;
import com.project.staynest.auth.crypto.encryption.model.EncryptionResultMap;
import com.project.staynest.auth.crypto.encryption.service.EncryptionService;
import com.project.staynest.auth.crypto.hashing.model.HashingResult;
import com.project.staynest.auth.crypto.hashing.model.HashingResultMap;
import com.project.staynest.auth.crypto.hashing.service.HashingService;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CryptoFacade {

    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final EncryptionService encryptionService;
    private final HashingService hashingService;
    public CryptoFacade(
            EncryptionService encryptionService,
            HashingService hashingService
    ){
        this.encryptionService = encryptionService;
        this.hashingService  = hashingService;
    }

    private List<String> getKeys(Map<String, ?> map){
        Validation.validate(map, "map", CLASS_NAME);
        return List.copyOf(map.keySet());
    }

    public boolean isLatestEncryption(short keyId, short version){
        return this.encryptionService.isLatest(keyId, version);
    }

    public EncryptionResultMap encryptDataMap(
            Map<String, String> plainDataMap
    ){
        Validation.validate(plainDataMap, "plainDataMap", CLASS_NAME);

        List<String> mapKeys = this.getKeys(plainDataMap);

        EncryptionResultMap encryptedDataMap = this.encryptionService.encrypt(
                plainDataMap
        );
        Validation.validate(encryptedDataMap, "encryptedDataMap", CLASS_NAME);

        for(String key : mapKeys){
            byte[] encryptedData = encryptedDataMap.encryptedDataMap().get(key);
            Validation.validate(encryptedData, "encryptedData", CLASS_NAME);
        }

        return encryptedDataMap;
    }

    public String decrypt(EncryptionResult encryptedData){
        Validation.validate(encryptedData, "encryptedData", CLASS_NAME);

        String decryptedData = this.encryptionService.decrypt(
                encryptedData
        );

        Validation.validate(decryptedData, "decryptedData", CLASS_NAME);

        return decryptedData;
    }

    public Map<String, String> decryptDataMap(
            Map<String, byte[]> encryptedData,
            short keyId,
            short version
    ){
        Validation.validate(encryptedData, "encryptedData", CLASS_NAME);

        List<String> mapKeys = this.getKeys(encryptedData);

        EncryptionResultMap encryptedDataMap = new EncryptionResultMap(
                encryptedData,
                keyId,
                version
        );

        Map<String, String> decryptedDataMap = this.encryptionService.decrypt(
                encryptedDataMap
        );
        Validation.validate(decryptedDataMap, "decryptedDataMap", CLASS_NAME);

        for(String key : mapKeys){
            String plainData = decryptedDataMap.get(key);

            Validation.validate(plainData, "plainData", CLASS_NAME);
        }

        return Map.copyOf(decryptedDataMap);
    }

    public EncryptionResultMap rotateDataMap(
            Map<String, byte[]> encryptedData,
            short keyId,
            short version
    ){
        Validation.validate(encryptedData, "encryptedData", CLASS_NAME);

        List<String> mapKeys = this.getKeys(encryptedData);

        EncryptionResultMap encryptedDataMap = new EncryptionResultMap(
                encryptedData,
                keyId,
                version
        );

        encryptedDataMap = this.encryptionService.rotate(
                encryptedDataMap
        );
        Validation.validate(encryptedDataMap, "encryptedDataMap", CLASS_NAME);

        for(String key : mapKeys){
            byte[] data = encryptedDataMap.encryptedDataMap().get(key);
            Validation.validate(data, "data", CLASS_NAME);
        }

        return encryptedDataMap;
    }

    public boolean isLatestHashing(short keyId, short version){
        return this.hashingService.isLatest(keyId, version);
    }

    public HashingResultMap hashDataMap(
            Map<String, String> plainDataMap
    ){
        Validation.validate(plainDataMap, "plainDataMap", CLASS_NAME);

        List<String> mapKeys = this.getKeys(plainDataMap);

        HashingResultMap hashDataMap = this.hashingService.hash(
                plainDataMap
        );
        Validation.validate(hashDataMap, "hashDataMap", CLASS_NAME);

        for(String key : mapKeys){
            byte[] dataHash = hashDataMap.hashDataMap().get(key);
            Validation.validate(dataHash, "dataHash", CLASS_NAME);
        }
        return hashDataMap;
    }

    public byte[] hashValue(String value){
        Validation.validate(value, "value", CLASS_NAME);

        byte[] valueHash = this.hashingService.hash(
                value
        ).hash();
        Validation.validate(valueHash, "valueHash", CLASS_NAME);
        return valueHash;
    }

    public List<HashingResult> getDetailedHashCandidates(String text){
        Validation.validate(text, "text", CLASS_NAME);

        List<HashingResult> hashDataList = this.hashingService.detailedHashCandidates(text);
        Validation.validate(hashDataList, "hashDataList", CLASS_NAME);

        return hashDataList;
    }

    public List<byte[]> getHashCandidates(String plainData){
        List<HashingResult> hashDataList = getDetailedHashCandidates(plainData);

        List<byte[]> hashCandidates = new ArrayList<>(hashDataList.size());

        for(HashingResult hashData : hashDataList){
            hashCandidates.add(hashData.hash());
        }

        return hashCandidates;
    }
}
