package com.project.staynest.auth.persistence.db.jpa.mapper;

import com.project.staynest.auth.persistence.db.jpa.entity.JwtEntity;
import com.project.staynest.auth.persistence.db.jpa.model.RefreshTokenData;

public class RefreshTokenDataMapper {
    private RefreshTokenDataMapper(){}

    public static RefreshTokenData from(JwtEntity jwtEntity){

        return new RefreshTokenData(
                jwtEntity.getRefreshJwtId(),
                jwtEntity.getRefreshExpiresAt(),
                jwtEntity.getStatus(),
                jwtEntity.getRole(),
                jwtEntity.getDeviceId(),
                jwtEntity.getUserAgent()
        );
    }
}
