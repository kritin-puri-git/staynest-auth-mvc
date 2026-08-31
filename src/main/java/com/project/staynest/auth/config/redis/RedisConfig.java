package com.project.staynest.auth.config.redis;

import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig  {

    private final RedisProperties redisProperties;

    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean(name = "masterRedisConnectionFactory")
    public RedisConnectionFactory masterRedisConnectionFactory(){

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        RedisProperties.Node master = redisProperties.getMaster();

        config.setHostName(master.getHost());
        config.setPort(master.getPort());
        config.setPassword(master.getPassword());
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "replicaRedisConnectionFactory")
    public RedisConnectionFactory replicaRedisConnectionFactory(){

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        RedisProperties.Node replica = redisProperties.getReplica();

        config.setHostName(replica.getHost());
        config.setPort(replica.getPort());
        config.setPassword(replica.getPassword());
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("masterRedisConnectionFactory") RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactory);

        setSerializers(redisTemplate);

        return redisTemplate;
    }


    @Bean(name = "masterRedisTemplate")
    public RedisTemplate<String, Object> masterRedisTemplate(
            @Qualifier("masterRedisConnectionFactory") RedisConnectionFactory connectionFactory){

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactory);

        setSerializers(redisTemplate);

        return redisTemplate;

    }

    @Bean(name = "replicaRedisTemplate")
    public RedisTemplate<String, Object> replicaRedisTemplate(
            @Qualifier("replicaRedisConnectionFactory") RedisConnectionFactory connectionFactory){

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactory);

        setSerializers(redisTemplate);

        return redisTemplate;

    }

    private void setSerializers(RedisTemplate<String, Object> redisTemplate){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        ObjectMapper objectMapper = JsonMapper.builder()
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS,false)
                .build();

        GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer = new GenericJacksonJsonRedisSerializer(objectMapper);

        redisTemplate.setValueSerializer(genericJacksonJsonRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJacksonJsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
    }
}
