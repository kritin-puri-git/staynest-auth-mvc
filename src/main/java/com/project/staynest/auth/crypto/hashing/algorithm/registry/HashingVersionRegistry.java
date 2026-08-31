package com.project.staynest.auth.crypto.hashing.algorithm.registry;

import com.project.staynest.auth.crypto.hashing.algorithm.HashingAlgorithm;
import com.project.staynest.auth.crypto.hashing.properties.HashingProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HashingVersionRegistry {

    private final short activeVersion;
    private final Map<Short, HashingAlgorithm> hashingAlgorithmMap;

    public HashingVersionRegistry(
            final List<HashingAlgorithm> hashingAlgorithms,
            final HashingProperties hashingProperties
    ){
        final Map<Short, HashingAlgorithm> hashingAlgorithmMap =
                new HashMap<>(hashingAlgorithms.size());
        for(HashingAlgorithm hashingAlgorithm : hashingAlgorithms){

            if(
                    hashingAlgorithmMap.put(
                            hashingAlgorithm.getVersion(),
                            hashingAlgorithm
                    ) !=null
            ){
                throw new IllegalStateException(
                        "Duplicate Hashing Algorithm versions found [" + this.getClass().getSimpleName() + "]"
                );
            }

        }

        for(short version : hashingProperties.getVersions().keySet()){
            if(!hashingAlgorithmMap.containsKey(version)){
                throw new IllegalStateException(
                        "Hashing algorithm version " + version + " is not registered.["
                                + this.getClass().getSimpleName() + "]"
                );
            }
        }

        if(!hashingAlgorithmMap.containsKey(hashingProperties.getActiveVersion())){
            throw new IllegalStateException(
                    "Active Hashing algorithm version " + hashingProperties.getActiveVersion() + " is not registered.["
                            + this.getClass().getSimpleName() + "]"
            );
        }

        this.activeVersion = hashingProperties.getActiveVersion();

        this.hashingAlgorithmMap = Map.copyOf(hashingAlgorithmMap);

    }

    public short getActiveVersion(){
        return this.activeVersion;
    }

    public Map<Short, HashingAlgorithm> getHashingAlgorithmMap(){
        return this.hashingAlgorithmMap;
    }
}
