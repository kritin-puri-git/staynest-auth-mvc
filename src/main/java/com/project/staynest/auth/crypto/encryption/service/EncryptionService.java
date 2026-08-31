package com.project.staynest.auth.crypto.encryption.service;

import com.project.staynest.auth.crypto.encryption.model.EncryptionResult;
import com.project.staynest.auth.crypto.encryption.model.EncryptionResultMap;

import java.util.Map;

public interface EncryptionService {

    EncryptionResult encrypt(String data);

    EncryptionResultMap encrypt(Map<String, String> dataMap);

    String decrypt(EncryptionResult encryptionResult);

    Map<String, String> decrypt(EncryptionResultMap encryptionResultMap);


    EncryptionResultMap rotate(EncryptionResultMap encryptionResultMap);

    boolean isLatest(short keyId, short version);

}