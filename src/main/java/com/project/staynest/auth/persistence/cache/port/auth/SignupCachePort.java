package com.project.staynest.auth.persistence.cache.port.auth;


import com.project.staynest.auth.persistence.cache.redis.model.signup.SignupCacheKeyData;
import com.project.staynest.auth.persistence.cache.redis.model.signup.SignupCacheSessionData;
import com.project.staynest.auth.persistence.cache.redis.model.signup.SignupCacheData;

public interface SignupCachePort {

    void saveSignupCache(SignupCacheSessionData signupCacheData);

    SignupCacheData getSignupData(SignupCacheKeyData keyData);

}
