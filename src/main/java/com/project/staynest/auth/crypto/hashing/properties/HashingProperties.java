package com.project.staynest.auth.crypto.hashing.properties;


import com.project.staynest.auth.validation.Validation;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "crypto.hashing")
public class HashingProperties {

    private Map<Short, byte[]> keys;
    private short activeVersion;
    private Map<Short, HashingVersionKeyProperties> versions;

    public void setKeys(Map<Short, String> keys){
        try{
            Validation.validate(keys, "keys", this.getClass().getSimpleName());
        }
        catch(IllegalArgumentException e){
            throw new IllegalStateException(
                    "Hashing keys not configured [" + this.getClass().getSimpleName() + "]",
                    e
            );
        }
        Map<Short, byte[]> keyBytes = new HashMap<>(keys.size());

        for(Map.Entry<Short, String> keysEntry : keys.entrySet()){
            try {

                keyBytes.put(
                        keysEntry.getKey(),
                        Base64.getDecoder().decode(keysEntry.getValue().trim())
                );

            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(
                        "Invalid Base64 Hashing key [" + this.getClass().getSimpleName() + "]",
                        e
                );
            }
        }

        this.keys = Map.copyOf(keyBytes);
    }

    public void setActiveVersion(Short activeVersion){
        if(activeVersion == null || activeVersion <= 0){
            throw new IllegalStateException(
                    "Hashing activeVersion not configured or invalid [" + this.getClass().getSimpleName() + "]"
            );
        }

        this.activeVersion = activeVersion;
    }

    public void setVersions(Map<Short, HashingVersionKeyProperties> versions){
        try{
            Validation.validate(versions, "versions", this.getClass().getSimpleName());
            this.versions = Map.copyOf(versions);
        }
        catch(IllegalArgumentException e){
            throw new IllegalStateException(
                    "Hashing versions not configured [" + this.getClass().getSimpleName() + "]",
                    e
            );
        }
    }

    public Map<Short, byte[]> getKeys() {

        Map<Short, byte[]> copy = new HashMap<>(this.keys.size());

        for (Map.Entry<Short, byte[]> entry : this.keys.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().clone());
        }

        return Map.copyOf(copy);

    }

    public short getActiveVersion() {
        return this.activeVersion;
    }

    public Map<Short, HashingVersionKeyProperties> getVersions() {
        return this.versions;
    }

    @PostConstruct
    private void validateConfiguration(){

        if(!this.versions.containsKey(this.activeVersion)){
            throw new IllegalStateException(
                    "Hashing active versions not found in existing versions [" + this.getClass().getSimpleName() + "]"
            );
        }

        for( Map.Entry<Short, HashingVersionKeyProperties> version : this.versions.entrySet()){
            HashingVersionKeyProperties properties = version.getValue();

            if(!properties.getKeyIds().contains(properties.getActiveKeyId())){
                throw new IllegalStateException(
                        "Active Key Id not present in hashing keyIds " +
                                "for version: " + version.getKey() + " [" + this.getClass().getSimpleName() + "]"
                );
            }
            for(short keyId : properties.getKeyIds()){
                if(!this.keys.containsKey(keyId)){
                    throw new IllegalStateException(
                            "Hashing KeyId: " + keyId + " referenced by version: "
                                    + version.getKey() +
                                    " is not configured under crypto.hashing.keys [" + this.getClass().getSimpleName() + "]"
                    );
                }
            }
        }

    }
}
