package com.project.staynest.auth.config.bucket4j;

import com.project.staynest.auth.config.redis.RedisProperties;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Bucket4jConfig {

    @Bean
    public RedisClient bucket4jRedisClient(
            RedisProperties redisProperties
    ){
        RedisProperties.Node master = redisProperties.getMaster();

        RedisURI redisURI = RedisURI.builder()
                .withHost(master.getHost())
                .withPort(master.getPort())
                .withPassword(master.getPassword().toCharArray())
                .build();

        return RedisClient.create(redisURI);
    }

    @Bean
    public StatefulRedisConnection<String, byte[]> bucket4jRedisConnection(
            RedisClient redisClient
    ){
        return redisClient.connect(
                RedisCodec.of(
                        StringCodec.UTF8,
                        ByteArrayCodec.INSTANCE
                )
        );
    }

    @Bean
    public ProxyManager<String> bucket4jProxyManager(
            StatefulRedisConnection<String, byte[]> connection
    ){
        return Bucket4jLettuce
                .casBasedBuilder(connection)
                .build();
    }
}
