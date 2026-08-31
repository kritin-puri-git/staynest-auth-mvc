package com.project.staynest.auth.business.service;

import com.project.staynest.auth.business.model.LatestTokenData;
import com.project.staynest.auth.business.model.jwt.JwtUserData;
import com.project.staynest.auth.enums.UserStatus;
import com.project.staynest.auth.business.model.jwt.JwtGenerationResult;

public interface JwtTokenService {
    JwtGenerationResult generateUserTokenDetails(
            String publicId,
            UserStatus status,
            String deviceId,
            String userAgent
    );
    LatestTokenData rotateJwt(JwtUserData tokenData);
}
