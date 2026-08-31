package com.project.staynest.auth.crypto.encryption.properties;


import com.project.staynest.auth.validation.Validation;

import java.util.HashSet;
import java.util.List;

public class EncryptionVersionKeyProperties {

    private short activeKeyId;
    private List<Short> keyIds;

    public void setActiveKeyId(Short activeKeyId){
        if(activeKeyId == null || activeKeyId <= 0){
            throw new IllegalStateException(
                    "Encryption activeKeyId not configured or invalid [" + this.getClass().getSimpleName() + "]"
            );
        }

        this.activeKeyId = activeKeyId;
    }

    public void setKeyIds(List<Short> keyIds) {

        try{
            Validation.validate(keyIds, "keyIds", this.getClass().getSimpleName());
        }
        catch(IllegalArgumentException e){
            throw new IllegalStateException(
                    "Encryption keyIds not configured [" + this.getClass().getSimpleName() + "]",
                    e
            );
        }

        if(keyIds.size() != new HashSet<>(keyIds).size()){
            throw new IllegalStateException(
                    "Duplicate Encryption keyIds found [" + this.getClass().getSimpleName() + "]."
            );
        }

        this.keyIds = List.copyOf(keyIds);
    }

    public short getActiveKeyId() {
        return this.activeKeyId;
    }

    public List<Short> getKeyIds() {
        return this.keyIds;
    }
}
