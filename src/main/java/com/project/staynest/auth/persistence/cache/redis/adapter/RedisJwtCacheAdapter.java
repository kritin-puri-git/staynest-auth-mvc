package com.project.staynest.auth.persistence.cache.redis.adapter;

import com.project.staynest.auth.persistence.cache.port.auth.JwtCachePort;
import com.project.staynest.auth.persistence.cache.redis.keys.RedisKeys;
import com.project.staynest.auth.persistence.cache.redis.mapper.jwt.JwtCacheMapper;
import com.project.staynest.auth.persistence.cache.redis.model.jwt.JwtCache;
import com.project.staynest.auth.persistence.cache.redis.model.jwt.JwtCacheData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisJwtCacheAdapter implements JwtCachePort {

    private final RedisTemplate<String,Object> redisTemplate;
    public RedisJwtCacheAdapter(
            @Qualifier("masterRedisTemplate")
            RedisTemplate<String, Object> redisTemplate
    ){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveJwtCache(JwtCacheData jwtCacheData) {

        String key = RedisKeys.getJwtSessionKey(jwtCacheData.subject(), jwtCacheData.sessionId());

        JwtCache jwtCache = JwtCacheMapper.from(jwtCacheData);

        this.redisTemplate.opsForValue()
                .set(
                        key,
                        jwtCache,
                        Duration.ofSeconds(jwtCacheData.ttl())
                );
    }
}
