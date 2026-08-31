package com.project.staynest.auth.config.bucket4j;

import io.github.bucket4j.BucketConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BucketConfigurations {

    @Bean(name = "signupIpBucketConfiguration")
    public BucketConfiguration signupIpBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(10)
                        .refillIntervally(5, Duration.ofMinutes(10))
                )
                .build();
    }
    @Bean(name = "signupEmailBucketConfiguration")
    public BucketConfiguration signupEmailBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(5)
                        .refillIntervally(5, Duration.ofMinutes(10))
                )
                .build();
    }
    @Bean(name = "verifySignupIpBucketConfiguration")
    public BucketConfiguration verifySignupIpBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(20)
                        .refillIntervally(5, Duration.ofMinutes(10))
                )
                .build();
    }
    @Bean(name = "verifySignupEmailBucketConfiguration")
    public BucketConfiguration verifySignupEmailBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(10)
                        .refillIntervally(5, Duration.ofMinutes(10))
                )
                .build();
    }

    @Bean(name = "loginIpBucketConfiguration")
    public BucketConfiguration loginIpBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(10)
                        .refillIntervally(5, Duration.ofMinutes(10))
                )
                .build();
    }
    @Bean(name = "loginEmailBucketConfiguration")
    public BucketConfiguration loginEmailBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(5)
                        .refillIntervally(5, Duration.ofMinutes(10))
                )
                .build();
    }
    @Bean(name = "verifyLoginIpBucketConfiguration")
    public BucketConfiguration verifyLoginIpBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(20)
                        .refillIntervally(5, Duration.ofMinutes(10))
                )
                .build();
    }

    @Bean(name = "verifyLoginEmailBucketConfiguration")
    public BucketConfiguration verifyLoginEmailBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(10)
                        .refillIntervally(5, Duration.ofMinutes(10))
                )
                .build();
    }

    @Bean(name = "refreshTokenIpBucketConfiguration")
    public BucketConfiguration refreshTokenIpBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(20)
                        .refillIntervally(20, Duration.ofMinutes(10))
                )
                .build();
    }

    @Bean(name = "refreshTokenDeviceIdBucketConfiguration")
    public BucketConfiguration refreshTokenDeviceIdBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(20)
                        .refillIntervally(20, Duration.ofMinutes(100))
                )
                .build();
    }

    @Bean(name = "refreshTokenSessionIdBucketConfiguration")
    public BucketConfiguration refreshTokenSessionIdBucketConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(20)
                        .refillIntervally(20, Duration.ofMinutes(100))
                )
                .build();
    }
}
