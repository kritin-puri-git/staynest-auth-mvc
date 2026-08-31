package com.project.staynest.auth.crypto.encryption.algorithm;

import com.project.staynest.auth.crypto.encryption.model.EncryptionData;

public interface EncryptionAlgorithm {

    short getVersion();
    short getActiveKeyId();
    EncryptionData encrypt(String data);
    byte[] encrypt(String data, short keyId);
    String decrypt(byte[] encryptedData, short keyId);
}
