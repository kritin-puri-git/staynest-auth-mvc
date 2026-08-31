package com.project.staynest.auth.business.service;

import io.github.bucket4j.BucketConfiguration;

public interface RateLimiterService {
    boolean tryConsume(String key, BucketConfiguration configuration);
}
