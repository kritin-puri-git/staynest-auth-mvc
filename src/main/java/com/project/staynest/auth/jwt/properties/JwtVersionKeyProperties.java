package com.project.staynest.auth.jwt.properties;

import com.project.staynest.auth.validation.Validation;

import java.util.HashSet;
import java.util.List;

public class JwtVersionKeyProperties {

    private short activeKeyId;
    private List<Short> keyIds;

    public void setActiveKeyId(Short activeKeyId){
        if(activeKeyId == null || activeKeyId <= 0){
            throw new IllegalStateException(
                    "Jwt activeKeyId not configured or invalid [" + this.getClass().getSimpleName() + "]"
            );
        }

        this.activeKeyId = activeKeyId;
    }
    public void setKeyIds(List<Short> keyIds){
        try{
            Validation.validate(keyIds, "keyIds", this.getClass().getSimpleName());
            if(keyIds.size() != new HashSet<>(keyIds).size()){
                throw new IllegalStateException(
                        "Duplicate Jwt keyIds found [" + this.getClass().getSimpleName() + "]."
                );
            }
            this.keyIds = keyIds;
        }
        catch(IllegalArgumentException e){
            throw new IllegalStateException(
                    "Jwt keyIds not configured [" + this.getClass().getSimpleName() + "]",
                    e
            );
        }
    }

    public short getActiveKeyId(){
        return this.activeKeyId;
    }
    public List<Short> getKeyIds(){
        return List.copyOf(this.keyIds);
    }
}