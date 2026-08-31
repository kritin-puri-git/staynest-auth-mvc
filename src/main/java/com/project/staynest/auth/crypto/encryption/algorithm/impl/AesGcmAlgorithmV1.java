package com.project.staynest.auth.crypto.encryption.algorithm.impl;

import com.project.staynest.auth.crypto.encryption.algorithm.EncryptionAlgorithm;
import com.project.staynest.auth.crypto.encryption.model.EncryptionData;
import com.project.staynest.auth.crypto.encryption.properties.EncryptionProperties;
import com.project.staynest.auth.crypto.encryption.properties.EncryptionVersionKeyProperties;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AesGcmAlgorithmV1 implements EncryptionAlgorithm {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final short ENCRYPTION_ALGORITHM_VERSION = 1;

    private final String CLASS_NAME = this.getClass().getSimpleName();

    private static final String SECRET_KEY_ALGORITHM =  "AES";

    private static SecretKey loadSecretKey(final byte[] keyBytes){

        if (keyBytes.length != 16 &&
                keyBytes.length != 24 &&
                keyBytes.length != 32) {

            throw new IllegalStateException(
                    "Invalid " + SECRET_KEY_ALGORITHM + " key length: " + keyBytes.length
                            + " bytes. Expected 16, 24, or 32 bytes for " +
                            "encryption algorithm version: " + ENCRYPTION_ALGORITHM_VERSION
                            + "[" + AesGcmAlgorithmV1.class.getSimpleName() + "]"
            );
        }
        return new SecretKeySpec(
                keyBytes,
                SECRET_KEY_ALGORITHM
        );
    }

    private final short activeKeyId;
    private final Map<Short, SecretKey> keys;

    public AesGcmAlgorithmV1(
            final EncryptionProperties encryptionProperties
    ){
        final EncryptionVersionKeyProperties properties = encryptionProperties.getVersions().get(this.getVersion());
        if (properties == null) {
            throw new IllegalStateException(
                    "Encryption algorithm version: " + this.getVersion()
                            + " is not configured [" + this.getClass().getSimpleName() + "]"
            );
        }

        final List<Short> keyIds = properties.getKeyIds();

        final Map<Short, byte[]> allKeys = encryptionProperties.getKeys();

        final Map<Short, SecretKey> keys = new HashMap<>(keyIds.size());
        for(short keyId : keyIds) {
            keys.put(
                    keyId,
                    loadSecretKey(
                            allKeys.get(keyId)
                    )
            );
        }

        this.keys = Map.copyOf(keys);

        this.activeKeyId = properties.getActiveKeyId();
    }

    @Override
    public short getVersion() {
        return ENCRYPTION_ALGORITHM_VERSION;
    }

    @Override
    public short getActiveKeyId(){
        return this.activeKeyId;
    }

    @Override
    public EncryptionData encrypt(final String data) {
        final short activeKeyId = this.activeKeyId;
        return new EncryptionData(
                encrypt(data, this.keys.get(activeKeyId)),
                activeKeyId
        );
    }

    @Override
    public byte[] encrypt(final String data, final short keyId) {

        final SecretKey secretKey =  this.keys.get(keyId);
        if(secretKey == null){
            throw new IllegalStateException(
                    "Encryption KeyId provided is not registered[" + CLASS_NAME + "]"
            );
        }
        return encrypt(data, secretKey);
    }

    @Override
    public String decrypt(final byte[] encryptedData, final short keyId) {
        final SecretKey secretKey =  this.keys.get(keyId);
        if(secretKey == null){
            throw new IllegalStateException(
                    "Decryption KeyId provided is not registered[" + CLASS_NAME + "]"
            );
        }
        return decrypt(encryptedData, secretKey);
    }

    private byte[] encrypt(final String data, final SecretKey secretKey) {
        Validation.validate(data, "data", CLASS_NAME);
        Validation.validate(secretKey, "secretKey", CLASS_NAME);

        try {

            final byte[] iv = new byte[IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(
                    TAG_LENGTH,
                    iv
            );

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    gcmParameterSpec
            );

            final byte[] cipherText = cipher.doFinal(
                    data.getBytes(StandardCharsets.UTF_8)
            );

            final byte[] encryptedData = new byte[iv.length + cipherText.length];

            System.arraycopy(
                    iv,
                    0,
                    encryptedData,
                    0,
                    iv.length
            );

            System.arraycopy(
                    cipherText,
                    0,
                    encryptedData,
                    iv.length,
                    cipherText.length
            );
            return encryptedData;

        }catch(
                NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException
                | BadPaddingException exception
        ){
            throw new IllegalStateException(
                    "Failed to encrypt data[" + CLASS_NAME + "]",
                    exception
            );
        }
    }

    private String decrypt(final byte[] encryptedData, final SecretKey secretKey) {
        Validation.validate(encryptedData, "encryptedData", CLASS_NAME);
        Validation.validate(secretKey, "secretKey", CLASS_NAME);

        if (encryptedData.length <= IV_LENGTH) {
            throw new IllegalStateException(
                    "Encrypted data is too short[" + CLASS_NAME + "]"
            );
        }
        try{

            final byte[] iv = new byte[IV_LENGTH];
            final byte[] encryptedPayload = new byte[ encryptedData.length - IV_LENGTH ];

            System.arraycopy(
                    encryptedData,
                    0,
                    iv,
                    0,
                    iv.length
            );

            System.arraycopy(
                    encryptedData,
                    iv.length,
                    encryptedPayload,
                    0,
                    encryptedPayload.length
            );

            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(
                    TAG_LENGTH,
                    iv
            );


            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    gcmParameterSpec
            );

            final byte[] plainTextBytes = cipher.doFinal(
                    encryptedPayload
            );

            return new String(
                    plainTextBytes,
                    StandardCharsets.UTF_8
            );

        }catch(
                NoSuchAlgorithmException
               | NoSuchPaddingException
               | InvalidKeyException
               | InvalidAlgorithmParameterException
               | IllegalBlockSizeException
               | BadPaddingException exception
        ){
            throw new IllegalStateException(
                    "Failed to decrypt data[" + CLASS_NAME + "]",
                    exception
            );
        }
    }

}
