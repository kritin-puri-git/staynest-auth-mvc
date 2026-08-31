package com.project.staynest.auth.presentation.util;

import com.project.staynest.auth.constants.CookieConstants;
import org.springframework.http.ResponseCookie;
import java.time.Duration;

public class ResponseCookieUtil {


    private ResponseCookieUtil(){}



    public static ResponseCookie generateRefreshTokenCookie(
            String publicUserId,
            String refreshToken,
            long refreshTokenAge
    ){

        return ResponseCookie.from(
                CookieConstants.getRefreshTokenCookieName(publicUserId),
                        refreshToken
                )
                .httpOnly(true)
                .secure(true)
                .sameSite(CookieConstants.SAME_SITE_COOKIE_ATTRIBUTE_VALUE_STRICT)
                .path("/v1/auth/refresh")
                .maxAge(refreshTokenAge)
                .build();
    }

    public static ResponseCookie generateActiveUserPublicIdCookie(
            String activeUserPublicId,
            long refreshTokenAge
    ){

        return ResponseCookie.from(CookieConstants.ACTIVE_USER_PUBLIC_ID_COOKIE_NAME, activeUserPublicId)
                .httpOnly(true)
                .secure(true)
                .sameSite(CookieConstants.SAME_SITE_COOKIE_ATTRIBUTE_VALUE_STRICT)
                .path("/v1/auth/")
                .maxAge(refreshTokenAge)
                .build();
    }

    public static ResponseCookie generateDeviceIdCookie(String deviceId){

        return ResponseCookie.from(CookieConstants.DEVICE_ID_COOKIE_NAME, deviceId)
                .httpOnly(true)
                .secure(true)
                .sameSite(CookieConstants.SAME_SITE_COOKIE_ATTRIBUTE_VALUE_STRICT)
                .path("/")
                .maxAge(Duration.ofDays(365))
                .build();
    }
}