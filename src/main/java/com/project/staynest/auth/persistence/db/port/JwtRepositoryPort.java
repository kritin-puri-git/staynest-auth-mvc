package com.project.staynest.auth.persistence.db.port;

import com.project.staynest.auth.persistence.db.jpa.model.JwtRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.JwtTokenData;

public interface JwtRepositoryPort {
    void saveJwtData(JwtTokenData jwtTokenData);
    void rotateJwt(JwtRotationData jwtRotationData);
}
