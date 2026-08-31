package com.project.staynest.auth.security.service.impl;

import com.project.staynest.auth.business.service.RateLimiterService;
import com.project.staynest.auth.constants.JwtConstants;
import com.project.staynest.auth.enums.JwtRole;
import com.project.staynest.auth.enums.UserStatus;
import com.project.staynest.auth.jwt.model.TokenClaims;
import com.project.staynest.auth.jwt.service.JwtService;
import com.project.staynest.auth.persistence.db.jpa.model.RefreshTokenData;
import com.project.staynest.auth.persistence.db.jpa.model.TokenIdentifier;
import com.project.staynest.auth.persistence.db.port.RefreshTokenAuthenticationPort;
import com.project.staynest.auth.security.model.JwtIdentity;
import com.project.staynest.auth.security.model.RefreshContext;
import com.project.staynest.auth.security.service.RefreshTokenAuthenticationService;
import com.project.staynest.auth.validation.Validation;
import io.github.bucket4j.BucketConfiguration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class RefreshTokenAuthenticationServiceImpl implements RefreshTokenAuthenticationService {

    private static final String SESSION_ID_RATE_LIMITER_KEY = "rate-limit:refresh:sessionId:";
    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final JwtService jwtService;
    private final RefreshTokenAuthenticationPort refreshTokenPort;
    private final RateLimiterService rateLimiterService;
    private final BucketConfiguration refreshTokenSessionIdBucketConfiguration;

    public RefreshTokenAuthenticationServiceImpl(
            JwtService jwtService,
            RefreshTokenAuthenticationPort refreshTokenPort,
            RateLimiterService rateLimiterService,
            BucketConfiguration refreshTokenSessionIdBucketConfiguration
    ){
        this.jwtService = jwtService;
        this.refreshTokenPort = refreshTokenPort;
        this.rateLimiterService = rateLimiterService;
        this.refreshTokenSessionIdBucketConfiguration = refreshTokenSessionIdBucketConfiguration;
    }

    @Override
    public JwtIdentity authenticate(RefreshContext refreshContext) {

        try {
            Validation.validate(refreshContext.refreshToken(), "refreshToken", CLASS_NAME);
            Validation.validate(refreshContext.deviceId(), "deviceId", CLASS_NAME);
            Validation.validate(refreshContext.userAgent(), "userAgent", CLASS_NAME);

            TokenClaims tokenClaims = this.jwtService.verify(refreshContext.refreshToken())
                    .orElseThrow(()-> new BadCredentialsException("Invalid RefreshToken"))
                    ;

            tryConsume(
                    SESSION_ID_RATE_LIMITER_KEY + tokenClaims.sessionId(),
                    refreshTokenSessionIdBucketConfiguration
            );

            RefreshTokenData refreshTokenData = this.refreshTokenPort.getRefreshTokenData(
                            new TokenIdentifier(
                                    tokenClaims.subject(),
                                    tokenClaims.sessionId()
                            )
                    )
                    .orElseThrow(()-> new BadCredentialsException("Invalid Credentials"));

            validateClaims(tokenClaims, refreshContext, refreshTokenData);

            return new JwtIdentity(
                    tokenClaims.subject(),
                    tokenClaims.sessionId(),
                    tokenClaims.role(),
                    refreshTokenData.status(),
                    tokenClaims.audience(),
                    refreshContext.deviceId(),
                    refreshContext.userAgent()
            );

        }catch (IllegalArgumentException ex){
            throw new BadCredentialsException("Invalid Credentials", ex);
        }
    }

    private void validateClaims(
            TokenClaims tokenClaims,
            RefreshContext refreshContext,
            RefreshTokenData refreshTokenData
    ){

        validateJwtType(tokenClaims.jwtType());

        validateJwtStatus(refreshTokenData.status());

        validateJwtRole(tokenClaims.role(), refreshTokenData.role());

        validateJwtAudience(tokenClaims.audience(), refreshTokenData.role());

        validateJwtId(tokenClaims.jwtTokenId(), refreshTokenData.refreshJwtTokenId());

        validateJwtExpiryDuration(tokenClaims.expirationTime(), tokenClaims.issueTime());

        validateJwtExpiry(
                tokenClaims.expirationTime().toInstant(),
                refreshTokenData.refreshExpirationTime()
        );

        validateDeviceId(refreshContext.deviceId(), refreshTokenData.deviceId());

        validateUserAgent(refreshContext.userAgent(), refreshTokenData.userAgent());
    }

    private void validateJwtType(String jwtType){
        if(!jwtType.equalsIgnoreCase(JwtConstants.JWT_REFRESH_TOKEN_TYPE))
            throw new BadCredentialsException("Invalid Jwt type");
    }

    private void validateJwtStatus(String jwtStatus){
        if(!jwtStatus.equalsIgnoreCase(UserStatus.ACTIVE.name()))
            throw new BadCredentialsException("Refresh token is not active");
    }

    private void validateJwtRole(String jwtRole, String validJwtRole){
        if (Arrays.stream(JwtRole.values())
                .noneMatch(role -> role.name().equalsIgnoreCase(jwtRole)))
            throw new BadCredentialsException("Invalid refresh token role");

        if(!jwtRole.equalsIgnoreCase(validJwtRole))
            throw new BadCredentialsException("Refresh token role not found");
    }

    private void validateJwtAudience(List<String> jwtAudienceList, String validRole){

        JwtRole jwtRole = JwtRole.from(validRole);

        List<String> validAudienceList = jwtRole.getAudience();

        int validListSize = validAudienceList.size();

        if(jwtAudienceList.size() !=  validAudienceList.size())
            throw new BadCredentialsException("Invalid Refresh token Audience List");

        for(int index = 0; index < validListSize; index++){
            if(!jwtAudienceList.get(index).equalsIgnoreCase(validAudienceList.get(index)))
                throw new BadCredentialsException("Invalid Refresh token Audience List");
        }
    }
    private void validateJwtId(String jwtId, String validJwtId){
        if(!jwtId.equalsIgnoreCase(validJwtId))
            throw new BadCredentialsException("Invalid refresh token id");
    }

    private void validateJwtExpiryDuration(Date issueTime, Date expiryTime){
        if(
                (expiryTime.getTime() - issueTime.getTime())/1000
                        > JwtConstants.JWT_REFRESH_TOKEN_EXPIRY_IN_SECONDS
        )
            throw new BadCredentialsException("Invalid refresh expiry time");
    }

    private void validateJwtExpiry(Instant expiry, Instant validExpiry){
        if(!expiry.equals(validExpiry))
            throw new BadCredentialsException("Invalid refresh token expiry");
    }

    private void validateDeviceId(String deviceId, String validDeviceId){
        if(!deviceId.equalsIgnoreCase(validDeviceId))
            throw new BadCredentialsException("Invalid device id");
    }

    private void validateUserAgent(String userAgent, String validUserAgent){
        if(!userAgent.equalsIgnoreCase(validUserAgent))
            throw new BadCredentialsException("Invalid user agent");
    }

    private void tryConsume(
            String key,
            BucketConfiguration ipConfiguration
    ){

        boolean allowed = this.rateLimiterService.tryConsume(
                key,
                ipConfiguration
        );

        if(!allowed)
            throw new IllegalStateException("Rate Limit Exceeded");
    }
}
