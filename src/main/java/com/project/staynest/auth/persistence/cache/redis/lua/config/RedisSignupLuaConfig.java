package com.project.staynest.auth.persistence.cache.redis.lua.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisSignupLuaConfig {

    @Bean
    public RedisScript<Long> saveSignupCacheScript(){
        String classPath = "auth/redis/lua/scripts/signup/save-signup-cache.lua";

        return generateScript(classPath);
    }

    public RedisScript<Long> generateScript(String classPath){
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();

        script.setLocation(
                new ClassPathResource(
                        classPath
                )

        );

        script.setResultType(Long.class);

        return script;

    }
}
