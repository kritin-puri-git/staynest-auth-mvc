package com.project.staynest.auth.persistence.cache.redis.lua.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisOtpLuaConfig {

    @Bean
    public RedisScript<Long> saveOtpScript(){
        String classPath = "auth/redis/lua/scripts/otp/save-otp.lua";

        return generateScript(classPath);
    }

    @Bean
    public RedisScript<Long> checkOtpValidScript(){
        String classPath = "auth/redis/lua/scripts/otp/check-otp-valid.lua";

        return generateScript(classPath);
    }

    @Bean
    public RedisScript<Long> resendOtpScript(){

        String classPath = "auth/redis/lua/scripts/otp/resend-otp.lua";

        return generateScript(classPath);
    }

    @Bean
    public RedisScript<Long> verifyOtpScript(){
        String classPath = "auth/redis/lua/scripts/otp/verify-otp.lua";

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
