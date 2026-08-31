package com.project.staynest.auth.jwt.signer;


import com.project.staynest.auth.jwt.model.TokenClaims;

import java.util.Optional;

public interface JwtSigner {
    short getVersion();
    String generateToken(TokenClaims tokenClaims);
    Optional<TokenClaims> verify(String Jwt);

}
