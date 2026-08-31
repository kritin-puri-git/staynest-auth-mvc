package com.project.staynest.auth.crypto.encryption.algorithm.registry;

import com.project.staynest.auth.crypto.encryption.algorithm.EncryptionAlgorithm;
import com.project.staynest.auth.crypto.encryption.properties.EncryptionProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EncryptionVersionRegistry {

    private final short activeVersion;
    private final Map<Short, EncryptionAlgorithm> encryptionAlgorithmMap;

    public EncryptionVersionRegistry(
            final List<EncryptionAlgorithm> encryptionAlgorithms,
            final EncryptionProperties encryptionProperties
    ){
        final Map<Short, EncryptionAlgorithm> encryptionAlgorithmMap =
                new HashMap<>(encryptionAlgorithms.size());
        for(EncryptionAlgorithm encryptionAlgorithm : encryptionAlgorithms){

            if(
            encryptionAlgorithmMap.put(
                    encryptionAlgorithm.getVersion(),
                    encryptionAlgorithm
            ) !=null
            ){
                throw new IllegalStateException(
                        "Duplicate Encryption Algorithm versions:"
                                + encryptionAlgorithm.getVersion() + " found ["
                                + this.getClass().getSimpleName() + "]"
                );
            }

        }

        for(short version : encryptionProperties.getVersions().keySet()){
            if(!encryptionAlgorithmMap.containsKey(version)){
                throw new IllegalStateException(
                        "Encryption algorithm version " + version + " is not registered.["
                                + this.getClass().getSimpleName() + "]"
                );
            }
        }

        if(!encryptionAlgorithmMap.containsKey(encryptionProperties.getActiveVersion())){
            throw new IllegalStateException(
                    "Active Encryption algorithm version "
                            + encryptionProperties.getActiveVersion() + " is not registered.["
                            + this.getClass().getSimpleName() + "]"
            );
        }

        this.activeVersion = encryptionProperties.getActiveVersion();

        this.encryptionAlgorithmMap = Map.copyOf(encryptionAlgorithmMap);

    }

    public short getActiveVersion(){
        return this.activeVersion;
    }

    public Map<Short, EncryptionAlgorithm> getEncryptionAlgorithmMap(){
        return this.encryptionAlgorithmMap;
    }

}
