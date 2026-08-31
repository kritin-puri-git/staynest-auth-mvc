package com.project.staynest.auth.security.service;

import com.project.staynest.auth.security.model.JwtIdentity;
import com.project.staynest.auth.security.model.RefreshContext;

public interface RefreshTokenAuthenticationService {

    JwtIdentity authenticate(RefreshContext refreshContext);
}
