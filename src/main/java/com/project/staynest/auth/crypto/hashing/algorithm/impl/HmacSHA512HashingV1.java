package com.project.staynest.auth.crypto.hashing.algorithm.impl;

import com.project.staynest.auth.crypto.hashing.algorithm.HashingAlgorithm;
import com.project.staynest.auth.crypto.hashing.model.HashingData;
import com.project.staynest.auth.crypto.hashing.properties.HashingProperties;
import com.project.staynest.auth.crypto.hashing.properties.HashingVersionKeyProperties;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class HmacSHA512HashingV1 implements HashingAlgorithm {

    private static final String SECRET_KEY_ALGORITHM =  "HmacSHA512";

    private static final String ALGORITHM = "HmacSHA512";
    private static final short HASHING_ALGORITHM_VERSION = 1;


    private static SecretKey loadSecretKey(final byte[] keyBytes){

        if (keyBytes.length < 32) {

            throw new IllegalStateException(
                    "Invalid " + SECRET_KEY_ALGORITHM + " key length: " + keyBytes.length
                            + " bytes. Expected at least 32 bytes for " +
                            "hashing algorithm version: " + HASHING_ALGORITHM_VERSION
            );
        }

        return new SecretKeySpec(
                keyBytes,
                SECRET_KEY_ALGORITHM
        );
    }


    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final short activeKeyId;
    private final Map<Short, SecretKey> keys;

    public HmacSHA512HashingV1(
            final HashingProperties hashingProperties
    ){
        final HashingVersionKeyProperties properties = hashingProperties.getVersions().get(this.getVersion());
        if (properties == null) {
            throw new IllegalStateException(
                    "Hashing algorithm version: " + this.getVersion()
                            + " is not configured [" + this.getClass().getSimpleName() + "]"
            );
        }

        final List<Short> keyIds = properties.getKeyIds();

        final Map<Short, byte[]> allKeys = hashingProperties.getKeys();

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
        return HASHING_ALGORITHM_VERSION;
    }

    @Override
    public short getActiveKeyId(){
        return this.activeKeyId;
    }

    @Override
    public HashingData hash(final String data) {
        final short activeKeyId = this.activeKeyId;

        return new HashingData(
                hash(data, this.keys.get(activeKeyId)),
                activeKeyId
        );
    }

    @Override
    public byte[] hash(final String data, final short keyId) {

        final SecretKey secretKey =  this.keys.get(keyId);
        if(secretKey == null){
            throw new IllegalStateException(
                    "Hashing KeyId provided is not available[" + CLASS_NAME + "]"
            );
        }
        return hash(data, secretKey);
    }

    @Override
    public List<HashingData> getHashCandidates(final String data){
        Map<Short, SecretKey> keys = this.keys;

        List<HashingData> hashingDataList = new ArrayList<>(keys.size());

        for(Map.Entry<Short, SecretKey> keysEntry : keys.entrySet()){
            hashingDataList.add(
                    new HashingData(
                            hash(data, keysEntry.getValue()),
                            keysEntry.getKey()
                    )

            );
        }

        return hashingDataList;
    }

    private byte[] hash(final String data, final SecretKey secretKey) {
        Validation.validate(data, "data", CLASS_NAME);
        Validation.validate(secretKey, "secretKey", CLASS_NAME);

        try{

            final Mac mac = Mac.getInstance(ALGORITHM);

            mac.init(secretKey);

            return mac.doFinal(
                    data.getBytes(StandardCharsets.UTF_8)
            );



        }catch(NoSuchAlgorithmException | InvalidKeyException exception){
            throw new IllegalStateException(
                    "Failed to hash data[" + CLASS_NAME + "]",
                    exception
            );
        }
    }
}
