package com.project.staynest.auth.business.service.impl;

import com.project.staynest.auth.business.service.RateLimiterService;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.stereotype.Service;


@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final ProxyManager<String> proxyManager;
    public RateLimiterServiceImpl(
            ProxyManager<String> proxyManager
    ){
        this.proxyManager = proxyManager;
    }

    @Override
    public boolean tryConsume(String key, BucketConfiguration configuration) {
        BucketProxy bucket = proxyManager
                .builder()
                .build(key, ()->configuration);
        return bucket.tryConsume(1);
    }
}
