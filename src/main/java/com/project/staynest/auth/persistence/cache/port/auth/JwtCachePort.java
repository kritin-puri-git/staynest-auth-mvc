package com.project.staynest.auth.persistence.cache.port.auth;

import com.project.staynest.auth.persistence.cache.redis.model.jwt.JwtCacheData;

public interface JwtCachePort {
    void saveJwtCache(JwtCacheData jwtCacheData);
}
