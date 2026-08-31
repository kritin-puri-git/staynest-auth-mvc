package com.project.staynest.auth.persistence.cache.redis.keys;

public final class RedisKeys {

    private RedisKeys(){}

    public static String getJwtSessionKey(String subject, String sessionId){
        return "auth:jwt:" + subject.trim() + ":" + sessionId.trim();
    }

    public static String getSignupKey(String identifier, String token){
        return "auth:signup:" + identifier.trim().toLowerCase() + ":" + token.trim();
    }

    public static String getOtpKey(String purpose, String identifier, String token){
        return "otp:" + purpose + ":" + identifier.trim().toLowerCase() + ":" + token.trim();
    }

    public static String getOtpVerifyRateLimitKey(String purpose, String identifier, String token){
        return "otp:ratelimit:verify:" + purpose + ":" + identifier.trim().toLowerCase() + ":" + token.trim();
    }
    public static String getOtpResendRateLimitKey(String purpose, String identifier, String token){
        return "otp:ratelimit:resend:" + purpose + ":" + identifier.trim().toLowerCase() + ":" + token.trim();
    }

}