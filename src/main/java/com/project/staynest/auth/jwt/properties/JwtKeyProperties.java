package com.project.staynest.auth.jwt.properties;

import java.util.Base64;

public class JwtKeyProperties {

    private byte[] privateKey;
    private byte[] publicKey;

    public void setPrivateKey(String privateKey){
        if(privateKey == null || privateKey.isBlank()){
            throw new IllegalStateException(
                    "Jwt privateKey not configured or found blank [" + this.getClass().getSimpleName() + "]"
            );
        }

        try {
            this.privateKey = Base64.getDecoder().decode(privateKey.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Invalid Base64 JWT privateKey [" + this.getClass().getSimpleName() + "]",
                    e
            );
        }
    }

    public void setPublicKey(String publicKey){
        if(publicKey == null || publicKey.isBlank()){
            throw new IllegalStateException(
                    "Jwt publicKey not configured or found blank [" + this.getClass().getSimpleName() + "]"
            );
        }

        try {
            this.publicKey = Base64.getDecoder().decode(publicKey.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Invalid Base64 JWT publicKey [" + this.getClass().getSimpleName() + "]",
                    e
            );
        }
    }

    public byte[] getPrivateKey(){
        return this.privateKey.clone();
    }

    public byte[] getPublicKey(){
        return this.publicKey.clone();
    }

}
