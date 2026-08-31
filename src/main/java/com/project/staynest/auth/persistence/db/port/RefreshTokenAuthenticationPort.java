package com.project.staynest.auth.persistence.db.port;

import com.project.staynest.auth.persistence.db.jpa.model.RefreshTokenData;
import com.project.staynest.auth.persistence.db.jpa.model.TokenIdentifier;

import java.util.Optional;

public interface RefreshTokenAuthenticationPort {

    Optional<RefreshTokenData> getRefreshTokenData(TokenIdentifier tokenIdentifier);
}
