package com.project.staynest.auth.jwt.service;

import com.project.staynest.auth.jwt.model.TokenClaims;

import java.util.Optional;

public interface JwtService {
    String generateToken(TokenClaims tokenClaims);
    Optional<TokenClaims> verify(String Jwt);
}
