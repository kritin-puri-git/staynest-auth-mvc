package com.project.staynest.auth.security.authentication;

import com.project.staynest.auth.security.model.JwtIdentity;
import com.project.staynest.auth.security.model.RefreshContext;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class RefreshTokenAuthentication extends AbstractAuthenticationToken {

    private final RefreshContext refreshContext;
    private JwtIdentity jwtIdentity;

    public RefreshTokenAuthentication(RefreshContext refreshContext){
        super((Collection<? extends GrantedAuthority>) null);
        this.refreshContext = refreshContext;
        jwtIdentity = null;
        setAuthenticated(false);
    }

    public void markAuthenticated(
            JwtIdentity jwtIdentity
    ){
        this.jwtIdentity = jwtIdentity;
        setAuthenticated(true);
    }

    @Override
    public @Nullable Object getCredentials() {
        return this.refreshContext;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return this.jwtIdentity;
    }
}
