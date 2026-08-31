package com.project.staynest.auth.jwt.signer.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.staynest.auth.errorhandling.exceptions.unexpected.UnexpectedIllegalStateException;
import com.project.staynest.auth.jwt.model.TokenClaims;
import com.project.staynest.auth.jwt.properties.JwtKeyProperties;
import com.project.staynest.auth.jwt.properties.JwtProperties;
import com.project.staynest.auth.jwt.properties.JwtVersionKeyProperties;
import com.project.staynest.auth.jwt.signer.JwtSigner;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

@Component
public class Ed25519JwtSignerV1 implements JwtSigner {
    private static final short JWT_SIGNER_VERSION = 1;
    private static final String JWT_CLAIM_SESSION_ID = "sessionId";
    private static final String JWT_CLAIM_ROLE = "role";
    private static final String JWT_CLAIM_TYPE = "type";
    private static final JWSAlgorithm JWT_SIGNING_ALGORITHM = JWSAlgorithm.Ed25519;
    private static final JOSEObjectType JOSE_JWT_TYPE = JOSEObjectType.JWT;
    private static final String JWT_HEADER_SIGNER_VERSION_PARAM = "signerVersion";

    private static boolean isValidKeyPair(
            final OctetKeyPair keyPair
    ) {
        try {
            final byte[] testData =
                    "key-pair-validation".getBytes(StandardCharsets.UTF_8);

            final JWSSigner signer =
                    new Ed25519Signer(keyPair);

            final JWSVerifier verifier =
                    new Ed25519Verifier(keyPair.toPublicJWK());

            final JWSHeader header =
                    new JWSHeader.Builder(JWSAlgorithm.Ed25519)
                            .build();

            final Base64URL signature =
                    signer.sign(header, testData);

            return verifier.verify(
                    header,
                    testData,
                    signature
            );
        } catch (JOSEException e) {
            throw new UnexpectedIllegalStateException(
                    "Invalid Ed25519 keyPair for version: "
                            + JWT_SIGNER_VERSION + "[" + Ed25519JwtSignerV1.class.getSimpleName() + "]",
                    e
            );
        }
    }

    private static OctetKeyPair getOctetKeyPair(final byte[] privateKeyBytes, final byte[] publicKeyBytes){
        if (privateKeyBytes == null || privateKeyBytes.length != 32) {
            throw new UnexpectedIllegalStateException(
                    "Ed25519 private key must be exactly 32 bytes for version: "
                            + JWT_SIGNER_VERSION + "[" + Ed25519JwtSignerV1.class.getSimpleName() + "]"
            );
        }

        if (publicKeyBytes == null || publicKeyBytes.length != 32) {
            throw new UnexpectedIllegalStateException(
                    "Ed25519 public key must be exactly 32 bytes for version: "
                            + JWT_SIGNER_VERSION + "[" + Ed25519JwtSignerV1.class.getSimpleName() + "]"
            );
        }

        final OctetKeyPair keyPair =  new OctetKeyPair.Builder(
                Curve.Ed25519,
                Base64URL.encode(publicKeyBytes)
        )
                .d(Base64URL.encode(privateKeyBytes))
                .build();

        if (isValidKeyPair(keyPair)) return keyPair;
        throw new UnexpectedIllegalStateException(
                "Invalid Ed25519 keyPair for version: "
                        + JWT_SIGNER_VERSION + Ed25519JwtSignerV1.class.getSimpleName()
        );
    }

    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final String jwtIssuer;
    private final short activeKeyId;
    private final Map<Short, OctetKeyPair> keySetMap;
    private final Map<Short, JWSSigner> keySignerMap;
    private final Map<Short, JWSVerifier> keyVerifierMap;
    public Ed25519JwtSignerV1(
            final JwtProperties jwtProperties
    ){
        final Map<Short, JwtKeyProperties> keys = jwtProperties.getKeys();

        final JwtVersionKeyProperties properties =
                jwtProperties
                        .getVersions()
                        .get(this.getVersion());

        if (properties == null) {
            throw new UnexpectedIllegalStateException(
                    "JWT signer version: " + this.getVersion()
                            + " is not configured [" + this.getClass().getSimpleName() + "]"
            );
        }

        final List<Short> keyIds = properties.getKeyIds();

        final Map<Short, OctetKeyPair> keySetMap = new HashMap<>(keyIds.size());
        final Map<Short, JWSSigner> keySignerMap = new HashMap<>(keyIds.size());
        final Map<Short, JWSVerifier> keyVerifierMap = new HashMap<>(keyIds.size());
        try{
            for(short keyId : keyIds ){
                final JwtKeyProperties jwtKeyProperties = keys.get(keyId);

                final OctetKeyPair keyPair = getOctetKeyPair(
                        jwtKeyProperties.getPrivateKey(),
                        jwtKeyProperties.getPublicKey()
                );

                keySetMap.put(
                        keyId,
                        keyPair
                );

                final JWSSigner keySigner = new Ed25519Signer(
                        keyPair
                );

                keySignerMap.put(
                        keyId,
                        keySigner
                );

                final JWSVerifier keyVerifier = new Ed25519Verifier(
                        keyPair.toPublicJWK()
                );

                keyVerifierMap.put(
                        keyId,
                        keyVerifier
                );
            }
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        this.keySetMap = Map.copyOf(keySetMap);
        this.keySignerMap = Map.copyOf(keySignerMap);
        this.keyVerifierMap = Map.copyOf(keyVerifierMap);

        this.activeKeyId = properties.getActiveKeyId();
        this.jwtIssuer = jwtProperties.getIssuer();
    }

    @Override
    public short getVersion() {
        return JWT_SIGNER_VERSION;
    }

    @Override
    public String generateToken(TokenClaims tokenClaims){
        Validation.validate(tokenClaims, "tokenClaims", CLASS_NAME);

        final JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(this.jwtIssuer)
                .jwtID(tokenClaims.jwtTokenId())
                .claim(JWT_CLAIM_SESSION_ID, tokenClaims.sessionId())
                .subject(tokenClaims.subject())
                .claim(JWT_CLAIM_ROLE, tokenClaims.role())
                .claim(JWT_CLAIM_TYPE, tokenClaims.jwtType())
                .audience(tokenClaims.audience())
                .issueTime(tokenClaims.issueTime())
                .expirationTime(tokenClaims.expirationTime())
                .build();

        return this.sign(claims);
    }

    private String sign(
            final JWTClaimsSet claims
    ) {
        final short activeKeyId = this.activeKeyId;

        final JWSHeader header =
                new JWSHeader
                        .Builder(JWT_SIGNING_ALGORITHM)
                        .type(JOSE_JWT_TYPE)
                        .keyID(String.valueOf(activeKeyId))
                        .customParam(JWT_HEADER_SIGNER_VERSION_PARAM, getVersion())
                        .build()
                ;

        final SignedJWT signedJWT = new SignedJWT(
                header,
                claims
        );

        final JWSSigner signer = keySignerMap.get(activeKeyId);
        try{
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }

        return signedJWT.serialize();
    }

    @Override
    public Optional<TokenClaims> verify(String jwt) {

        try{
            final SignedJWT signedJwt = SignedJWT.parse(jwt);

            if(!validateJwtHeader(signedJwt.getHeader())) return Optional.empty();

            final short keyId = Short.parseShort(signedJwt.getHeader().getKeyID());

            final JWSVerifier verifier = this.keyVerifierMap.get(keyId);

            try{
                Validation.validate(verifier, "verifier", CLASS_NAME);
            }catch (IllegalArgumentException exception){
                return Optional.empty();
            }

            if(!signedJwt.verify(verifier)) return Optional.empty();

            return this.verifyClaims(signedJwt);
        }catch(ParseException | JOSEException | NumberFormatException exception){
            return Optional.empty();
        }
    }

    private Optional<TokenClaims> verifyClaims(SignedJWT signedJWT){
        try {
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            String jwtIssuer = claims.getIssuer();
            String jwtTokenId = claims.getJWTID();
            String sessionId = claims.getStringClaim(JWT_CLAIM_SESSION_ID);
            String subject = claims.getSubject();
            List<String> audience = claims.getAudience();
            String role = claims.getStringClaim(JWT_CLAIM_ROLE);
            String jwtType = claims.getStringClaim(JWT_CLAIM_TYPE);

            Date expirationTime = claims.getExpirationTime();
            Date issueTime = claims.getIssueTime();

            Validation.validate(jwtIssuer, "jwtIssuer", CLASS_NAME);
            Validation.validate(expirationTime, "expirationTime", CLASS_NAME);
            Validation.validate(issueTime, "issueTime", CLASS_NAME);
            if(!this.jwtIssuer.equalsIgnoreCase(jwtIssuer)) return Optional.empty();

            Instant now = Instant.now();

            if(issueTime.after(Date.from(now)) || expirationTime.before(Date.from(now)))
                return Optional.empty();

            return Optional.of(
                    new TokenClaims(
                            jwtTokenId,
                            subject,
                            sessionId,
                            audience,
                            role,
                            jwtType,
                            issueTime,
                            expirationTime
                    )
            );

        }catch (ParseException | IllegalArgumentException exception){
            return Optional.empty();
        }
    }

    private boolean validateJwtHeader(JWSHeader header){
            final Object versionObj = header.getCustomParam(JWT_HEADER_SIGNER_VERSION_PARAM);

            try{
                Validation.validate(versionObj, "versionObj", CLASS_NAME);
            }catch (IllegalArgumentException exception){
                return false;
            }
            final short version = Short.parseShort(versionObj.toString());
            final JWSAlgorithm signerAlgorithm = header.getAlgorithm();
            final short keyId = Short.parseShort(header.getKeyID());
            final JOSEObjectType objectType = header.getType();

            if(version != this.getVersion()) return false;
            if(!keySetMap.containsKey(keyId)) return false;
            if(!JWT_SIGNING_ALGORITHM.equals(signerAlgorithm)) return false;
            if(!JOSE_JWT_TYPE.equals(objectType)) return false;

            return true;
    }
}