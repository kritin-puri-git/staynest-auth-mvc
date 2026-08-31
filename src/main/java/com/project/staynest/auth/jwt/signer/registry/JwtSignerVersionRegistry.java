package com.project.staynest.auth.jwt.signer.registry;

import com.project.staynest.auth.jwt.properties.JwtProperties;
import com.project.staynest.auth.jwt.signer.JwtSigner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtSignerVersionRegistry {

    private final short activeVersion;
    private final Map<Short, JwtSigner> jwtSignersMap;
    public JwtSignerVersionRegistry(
            final List<JwtSigner> jwtSigners,
            final JwtProperties jwtProperties
    ){
        this.activeVersion = jwtProperties.getActiveVersion();

        final Map<Short, JwtSigner> jwtSignersMap = new HashMap<>(jwtSigners.size());
        for(JwtSigner jwtSigner : jwtSigners){
            if(jwtSignersMap.put(jwtSigner.getVersion(), jwtSigner) != null){
                throw new IllegalStateException(
                        "Duplicate Jwt signer versions found [" + this.getClass().getSimpleName() + "]"
                );
            }
        }
        this.jwtSignersMap = Map.copyOf(jwtSignersMap);

        if (!this.jwtSignersMap.containsKey(this.activeVersion)) {
            throw new IllegalStateException(
                    "Active JWT signer version " + activeVersion + " is not registered.["
                            + this.getClass().getSimpleName() + "]"
            );
        }
    }

    public short getActiveVersion(){
        return this.activeVersion;
    }
    public Map<Short, JwtSigner> getJwtSignersMap(){
        return this.jwtSignersMap;
    }
}