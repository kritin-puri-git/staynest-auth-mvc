package com.project.staynest.auth.crypto.encryption.service.impl;

import com.project.staynest.auth.crypto.encryption.algorithm.EncryptionAlgorithm;
import com.project.staynest.auth.crypto.encryption.algorithm.registry.EncryptionVersionRegistry;
import com.project.staynest.auth.crypto.encryption.model.EncryptionData;
import com.project.staynest.auth.crypto.encryption.model.EncryptionResult;
import com.project.staynest.auth.crypto.encryption.model.EncryptionResultMap;
import com.project.staynest.auth.crypto.encryption.service.EncryptionService;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EncryptionServiceImpl implements EncryptionService {


    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final short activeVersion;
    private final Map<Short, EncryptionAlgorithm> encryptionAlgorithms;
    public EncryptionServiceImpl(
        EncryptionVersionRegistry encryptionVersionRegistry
    ){
        this.activeVersion = encryptionVersionRegistry.getActiveVersion();
        this.encryptionAlgorithms = encryptionVersionRegistry.getEncryptionAlgorithmMap();
    }

    @Override
    public EncryptionResult encrypt(final String data) {
        Validation.validate(data, "data", CLASS_NAME);

        final short activeVersion = this.activeVersion;
        final EncryptionAlgorithm activeEncryptionAlgorithm = this.encryptionAlgorithms.get(activeVersion);

        final EncryptionData encryptionData = activeEncryptionAlgorithm.encrypt(
                data
        );

        return new EncryptionResult(
                encryptionData.encryptedData(),
                encryptionData.keyId(),
                activeVersion
        );
    }

    @Override
    public EncryptionResultMap encrypt(final Map<String, String> dataMap) {
        Validation.validate(dataMap, "dataMap", CLASS_NAME);

        final short activeVersion = this.activeVersion;
        final EncryptionAlgorithm activeEncryptionAlgorithm = this.encryptionAlgorithms.get(activeVersion);

        final short activeKeyId = activeEncryptionAlgorithm.getActiveKeyId();
        final Map<String, byte[]> encryptedDataMap = new HashMap<>(dataMap.size());
        for(Map.Entry<String, String> dataMapEntry : dataMap.entrySet()){
            encryptedDataMap.put(
                    dataMapEntry.getKey(),
                    activeEncryptionAlgorithm.encrypt(
                            dataMapEntry.getValue(),
                            activeKeyId
                    )
            );
        }

        return new EncryptionResultMap(
                encryptedDataMap,
                activeKeyId,
                activeVersion
        );
    }

    @Override
    public String decrypt(final EncryptionResult encryptionResult) {
        Validation.validate(encryptionResult, "encryptionResult", CLASS_NAME);

        final EncryptionAlgorithm encryptionAlgorithm = this.encryptionAlgorithms.get(
                encryptionResult.version()
        );

        Validation.validate(encryptionAlgorithm, "encryptionAlgorithm", CLASS_NAME);

        return encryptionAlgorithm.decrypt(
                encryptionResult.encryptedData(),
                encryptionResult.keyId()
        );
    }

    @Override
    public Map<String, String> decrypt(final EncryptionResultMap encryptionResultMap) {
        Validation.validate(encryptionResultMap, "encryptionResultMap", CLASS_NAME);

        final EncryptionAlgorithm encryptionAlgorithm = this.encryptionAlgorithms.get(
                encryptionResultMap.version()
        );

        Validation.validate(encryptionAlgorithm, "encryptionAlgorithm", CLASS_NAME);

        final Map<String, String> dataMap = new HashMap<>(encryptionResultMap.encryptedDataMap().size());

        for(Map.Entry<String, byte[]> encryptedDataEntry : encryptionResultMap.encryptedDataMap().entrySet()){

            dataMap.put(
                    encryptedDataEntry.getKey(),
                    encryptionAlgorithm.decrypt(
                            encryptedDataEntry.getValue(),
                            encryptionResultMap.keyId()
                    )
            );

        }

        return Map.copyOf(dataMap);
    }

    @Override
    public EncryptionResultMap rotate(final EncryptionResultMap encryptionResultMap) {
        Validation.validate(encryptionResultMap, "encryptionResultMap", CLASS_NAME);
        return encrypt(decrypt(encryptionResultMap));
    }

    @Override
    public boolean isLatest(final short keyId, final short version) {

        final short activeVersion = this.activeVersion;
        final EncryptionAlgorithm activeEncryptionAlgorithm = this.encryptionAlgorithms.get(activeVersion);

        return keyId == activeEncryptionAlgorithm.getActiveKeyId()
                && version == activeVersion;
    }
}
