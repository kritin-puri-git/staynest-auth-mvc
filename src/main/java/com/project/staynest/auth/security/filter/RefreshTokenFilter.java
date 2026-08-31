package com.project.staynest.auth.security.filter;

import com.project.staynest.auth.business.service.RateLimiterService;
import com.project.staynest.auth.constants.CookieConstants;
import com.project.staynest.auth.enums.UserAgentStatus;
import com.project.staynest.auth.security.authentication.RefreshTokenAuthentication;
import com.project.staynest.auth.security.model.RefreshContext;
import com.project.staynest.auth.validation.Validation;
import io.github.bucket4j.BucketConfiguration;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;

@Component
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final static String IP_RATE_LIMITER_KEY = "rate-limit:refresh:ip:";
    private final static String DEVICE_ID_RATE_LIMITER_KEY = "rate-limit:refresh:device:";

    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final AuthenticationManager authenticationManager;
    private final RateLimiterService rateLimiterService;
    private final BucketConfiguration refreshTokenIpBucketConfiguration;
    private final BucketConfiguration refreshTokenDeviceIdBucketConfiguration;
    public RefreshTokenFilter(
            AuthenticationManager authenticationManager,
            RateLimiterService rateLimiterService,
            BucketConfiguration refreshTokenIpBucketConfiguration,
            BucketConfiguration refreshTokenDeviceIdBucketConfiguration
    ){
        this.authenticationManager = authenticationManager;
        this.rateLimiterService = rateLimiterService;
        this.refreshTokenIpBucketConfiguration = refreshTokenIpBucketConfiguration;
        this.refreshTokenDeviceIdBucketConfiguration = refreshTokenDeviceIdBucketConfiguration;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        if(cookies == null || cookies.length==0)
            throw new BadCredentialsException("No cookie found");

        try {

            String ip = request.getRemoteAddr();
            Validation.validate(ip, "ip", CLASS_NAME);

            tryConsume(
                    IP_RATE_LIMITER_KEY + ip,
                    this.refreshTokenIpBucketConfiguration
            );

            String userAgent = request.getHeader(CookieConstants.CLIENT_USER_AGENT);
            if(userAgent == null || userAgent.isBlank())
                userAgent = UserAgentStatus.NOT_AVAILABLE.name();

            RefreshContext refreshContext = getRefreshContext(cookies, userAgent);

            tryConsume(
                    DEVICE_ID_RATE_LIMITER_KEY + refreshContext.deviceId(),
                    this.refreshTokenDeviceIdBucketConfiguration
            );

            RefreshTokenAuthentication refreshAuthentication =
                    new RefreshTokenAuthentication(refreshContext);

            Authentication authentication = authenticationManager.authenticate(
                    refreshAuthentication
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }
        catch (IllegalArgumentException ex){
            throw new BadCredentialsException("Invalid Cookie Credentials", ex);
        }
    }

    private RefreshContext getRefreshContext(final Cookie[] cookies, String userAgent){

        String deviceId = null;
        String activeUserId = null;
        HashMap<String, String> refreshTokens = new HashMap<>(cookies.length);

        for(Cookie cookie: cookies){
            if(cookie.getName().equalsIgnoreCase(CookieConstants.DEVICE_ID_COOKIE_NAME))
                deviceId = cookie.getValue();

            if(cookie.getName().equalsIgnoreCase(CookieConstants.ACTIVE_USER_PUBLIC_ID_COOKIE_NAME))
                activeUserId = cookie.getValue();

            if(cookie.getName().startsWith(CookieConstants.REFRESH_TOKEN_COOKIE_NAME_STARTS_WITH))
                refreshTokens.put(cookie.getName(), cookie.getValue());
        }

        Validation.validate(deviceId, "deviceId", CLASS_NAME);
        Validation.validate(activeUserId, "activeUserId", CLASS_NAME);
        Validation.validate(refreshTokens, "refreshTokens", CLASS_NAME);

        String refreshToken = refreshTokens.get(
                CookieConstants.getRefreshTokenCookieName(activeUserId)
        );

        Validation.validate(refreshToken, "refreshToken", CLASS_NAME);

        return new RefreshContext(
                refreshToken,
                deviceId,
                userAgent
        );
    }

    private void tryConsume(
            String key,
            BucketConfiguration ipConfiguration
    ){

        boolean allowed = this.rateLimiterService.tryConsume(
                key,
                ipConfiguration
        );

        if(!allowed)
            throw new IllegalStateException("Rate Limit Exceeded");
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request){
        return !request.getRequestURI().equalsIgnoreCase("/v1/auth/refresh");
    }
}