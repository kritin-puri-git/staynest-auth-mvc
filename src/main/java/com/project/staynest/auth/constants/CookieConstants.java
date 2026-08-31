package com.project.staynest.auth.constants;

public class CookieConstants {
    private CookieConstants(){}

    public static final String REFRESH_TOKEN_COOKIE_NAME_STARTS_WITH = "refresh";
    public static final String ACTIVE_USER_PUBLIC_ID_COOKIE_NAME = "activeUserPublicId";
    public static final String DEVICE_ID_COOKIE_NAME = "deviceId";
    public static final String CLIENT_USER_AGENT = "user-agent";
    public static final String SAME_SITE_COOKIE_ATTRIBUTE_VALUE_STRICT = "strict";

    public static String getRefreshTokenCookieName(
            String publicUserId
    ){
        return REFRESH_TOKEN_COOKIE_NAME_STARTS_WITH + "-" + publicUserId;
    }
}
