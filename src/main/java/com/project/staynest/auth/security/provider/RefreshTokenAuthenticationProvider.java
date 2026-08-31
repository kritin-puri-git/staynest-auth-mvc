package com.project.staynest.auth.security.provider;

import com.project.staynest.auth.security.authentication.RefreshTokenAuthentication;
import com.project.staynest.auth.security.model.JwtIdentity;
import com.project.staynest.auth.security.model.RefreshContext;
import com.project.staynest.auth.security.service.RefreshTokenAuthenticationService;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final RefreshTokenAuthenticationService refreshAuthenticationService;
    public RefreshTokenAuthenticationProvider(
            RefreshTokenAuthenticationService refreshAuthenticationService
    ){
        this.refreshAuthenticationService = refreshAuthenticationService;
    }

    @Override
    public @Nullable Authentication authenticate(final @NonNull Authentication authentication)
            throws AuthenticationException {

        final RefreshTokenAuthentication refreshAuthentication =
                (RefreshTokenAuthentication) authentication;

        final RefreshContext refreshContext = (RefreshContext) refreshAuthentication.getCredentials();

        final JwtIdentity jwtIdentity =
                this.refreshAuthenticationService.authenticate(refreshContext);

        refreshAuthentication.markAuthenticated(
                jwtIdentity
        );

        return refreshAuthentication;
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return RefreshTokenAuthentication.class
                .isAssignableFrom(authentication);
    }
}