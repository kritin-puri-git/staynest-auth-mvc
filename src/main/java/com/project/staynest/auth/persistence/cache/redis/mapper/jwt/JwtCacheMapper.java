package com.project.staynest.auth.persistence.cache.redis.mapper.jwt;

import com.project.staynest.auth.persistence.cache.redis.model.jwt.JwtCache;
import com.project.staynest.auth.persistence.cache.redis.model.jwt.JwtCacheData;

public class JwtCacheMapper {
    private JwtCacheMapper(){}

    public static JwtCache from(JwtCacheData jwtCacheData){

        return new JwtCache(
                jwtCacheData.accessTokenJti(),
                jwtCacheData.status(),
                jwtCacheData.deviceId(),
                jwtCacheData.userAgent()
        );
    }
}
