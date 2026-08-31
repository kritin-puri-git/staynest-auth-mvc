package com.project.staynest.auth.jwt.properties;

import com.project.staynest.auth.validation.Validation;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String issuer;
    private Map<Short, JwtKeyProperties> keys;
    private short activeVersion;
    private Map<Short, JwtVersionKeyProperties> versions;

    public void setIssuer(String issuer){
        if(issuer == null || issuer.isBlank()){
            throw new IllegalStateException(
                    "Jwt issuer not configured or found blank [" + this.getClass().getSimpleName() + "]"
            );
        }
        this.issuer = issuer;
    }
    public void setKeys(Map<Short, JwtKeyProperties> keys){
        try{
            Validation.validate(keys, "keys", this.getClass().getSimpleName());
            this.keys = Map.copyOf(keys);
        }
        catch(IllegalArgumentException e){
            throw new IllegalStateException(
                    "Jwt keys not configured [" + this.getClass().getSimpleName() + "]",
                    e
            );
        }
    }

    public void setActiveVersion(Short activeVersion){
        if(activeVersion == null || activeVersion <= 0){
            throw new IllegalStateException(
                    "Jwt activeVersion not configured or invalid [" + this.getClass().getSimpleName() + "]"
            );
        }

        this.activeVersion = activeVersion;
    }

    public void setVersions(Map<Short, JwtVersionKeyProperties> versions){
        try{
            Validation.validate(versions, "versions", this.getClass().getSimpleName());
            this.versions = Map.copyOf(versions);
        }
        catch(IllegalArgumentException e){
            throw new IllegalStateException(
                    "Jwt versions not configured [" + this.getClass().getSimpleName() + "]",
                    e
            );
        }
    }


    public String getIssuer(){
        return this.issuer;
    }
    public Map<Short, JwtKeyProperties> getKeys(){
        return this.keys;
    }
    public short getActiveVersion(){
        return this.activeVersion;
    }
    public Map<Short, JwtVersionKeyProperties> getVersions(){
        return this.versions;
    }

    @PostConstruct
    private void validateConfiguration(){

        if(!this.versions.containsKey(this.activeVersion)){
            throw new IllegalStateException(
                    "Jwt active versions not found in existing versions [" + this.getClass().getSimpleName() + "]"
            );
        }

        for( Map.Entry<Short, JwtVersionKeyProperties> version : this.versions.entrySet()){
            JwtVersionKeyProperties properties = version.getValue();

            if(!properties.getKeyIds().contains(properties.getActiveKeyId())){
                throw new IllegalStateException(
                        "Active Key Id not present in Jwt KeyIds " +
                                "for version: " + version.getKey() + " [" + this.getClass().getSimpleName() + "]"
                );
            }
            for(short keyId : properties.getKeyIds()){
                if(!this.keys.containsKey(keyId)){
                    throw new IllegalStateException(
                            "Jwt KeyId: " + keyId + " referenced by version: "
                                    + version.getKey() +
                                    " is not configured under jwt.keys [" + this.getClass().getSimpleName() + "]"
                    );
                }
            }
        }

    }
}