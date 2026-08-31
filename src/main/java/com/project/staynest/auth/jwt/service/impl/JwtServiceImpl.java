package com.project.staynest.auth.jwt.service.impl;

import com.project.staynest.auth.jwt.model.TokenClaims;
import com.project.staynest.auth.jwt.service.JwtService;
import com.project.staynest.auth.jwt.signer.JwtSigner;
import com.project.staynest.auth.jwt.signer.registry.JwtSignerVersionRegistry;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class JwtServiceImpl implements JwtService {

    private static final String JWT_VERSION_SEPARATOR = "~";

    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final short activeSignerVersion;
    private final Map<Short, JwtSigner> jwtSignerMap;

    public JwtServiceImpl(
            JwtSignerVersionRegistry versionRegistry
    ){
        this.activeSignerVersion = versionRegistry.getActiveVersion();
        this.jwtSignerMap = Map.copyOf(versionRegistry.getJwtSignersMap());
    }

    @Override
    public String generateToken(final TokenClaims tokenClaims){
        Validation.validate(tokenClaims, "tokenClaims", CLASS_NAME);

        final short activeSignerVersion = this.activeSignerVersion;
        final JwtSigner activeJwtSigner = this.jwtSignerMap.get(activeSignerVersion);

        return activeSignerVersion
                + JWT_VERSION_SEPARATOR
                + activeJwtSigner.generateToken(tokenClaims);
    }

    @Override
    public Optional<TokenClaims> verify(final String jwt) {
        final int separatorIndex = jwt.indexOf(JWT_VERSION_SEPARATOR);

        if (separatorIndex <= 0 || separatorIndex == jwt.length() - 1) {
            return Optional.empty();
        }

        final String versionString = jwt.substring(0, separatorIndex);
        final String signedJwt = jwt.substring(separatorIndex + 1);

        try {
            final short version = Short.parseShort(versionString);

            final JwtSigner jwtSigner = this.jwtSignerMap.get(version);

            Validation.validate(jwtSigner, "jwtSigner", CLASS_NAME);

            return jwtSigner.verify(signedJwt);
        }catch(IllegalArgumentException exception){
            return Optional.empty();
        }
    }
}