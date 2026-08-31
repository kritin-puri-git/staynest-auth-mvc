package com.project.staynest.auth.presentation.controller;

import com.project.staynest.auth.business.mapper.login.LoginByEmailModelMapper;
import com.project.staynest.auth.business.mapper.login.VerifyLoginByEmailModelMapper;
import com.project.staynest.auth.business.model.SentOtpData;
import com.project.staynest.auth.business.model.SigninData;
import com.project.staynest.auth.business.service.EmailLoginService;
import com.project.staynest.auth.business.service.RateLimiterService;
import com.project.staynest.auth.constants.CookieConstants;
import com.project.staynest.auth.enums.UserAgentStatus;
import com.project.staynest.auth.presentation.dto.request.LoginRequestDto;
import com.project.staynest.auth.presentation.dto.request.VerifyEmailOtpRequestDto;
import com.project.staynest.auth.presentation.dto.response.SigninResponseDTO;
import com.project.staynest.auth.presentation.dto.response.wrapper.ApiResponseDTO;
import com.project.staynest.auth.presentation.util.SigninResponseUtil;
import com.project.staynest.auth.validation.Validation;
import io.github.bucket4j.BucketConfiguration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
public class LoginController {

    private static final String LOGIN_IP_RATE_LIMITER_KEY = "login:ip:";
    private static final String LOGIN_EMAIL_RATE_LIMITER_KEY = "login:email:";
    private static final String VERIFY_LOGIN_IP_RATE_LIMITER_KEY = "verify-login:ip:";
    private static final String VERIFY_LOGIN_EMAIL_RATE_LIMITER_KEY = "verify-login:email:";

    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final EmailLoginService emailLoginService;
    private final RateLimiterService rateLimiterService;
    private final BucketConfiguration loginIpBucketConfiguration;
    private final BucketConfiguration loginEmailBucketConfiguration;
    private final BucketConfiguration verifyLoginIpBucketConfiguration;
    private final BucketConfiguration verifyLoginEmailBucketConfiguration;
    public LoginController(
            EmailLoginService emailLoginService,
            RateLimiterService rateLimiterService,
            @Qualifier("loginIpBucketConfiguration")
            BucketConfiguration loginIpBucketConfiguration,
            @Qualifier("loginEmailBucketConfiguration")
            BucketConfiguration loginEmailBucketConfiguration,
            @Qualifier("verifyLoginIpBucketConfiguration")
            BucketConfiguration verifyLoginIpBucketConfiguration,
            @Qualifier("verifyLoginEmailBucketConfiguration")
            BucketConfiguration verifyLoginEmailBucketConfiguration
    ){
        this.emailLoginService = emailLoginService;
        this.rateLimiterService = rateLimiterService;
        this.loginIpBucketConfiguration = loginIpBucketConfiguration;
        this.loginEmailBucketConfiguration = loginEmailBucketConfiguration;
        this.verifyLoginIpBucketConfiguration = verifyLoginIpBucketConfiguration;
        this.verifyLoginEmailBucketConfiguration = verifyLoginEmailBucketConfiguration;
    }

    @PostMapping("/email-login")
    public ResponseEntity<ApiResponseDTO<SentOtpData>> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            HttpServletRequest request
    ){

        String ip = request.getRemoteAddr();
        Validation.validate(ip, "ip", CLASS_NAME);

        tryConsume(
                LOGIN_IP_RATE_LIMITER_KEY + ip,
                loginIpBucketConfiguration,
                LOGIN_EMAIL_RATE_LIMITER_KEY + loginRequest.getEmail(),
                loginEmailBucketConfiguration
        );

        SentOtpData sentOtpData = this.emailLoginService.loginByEmail(
                LoginByEmailModelMapper.from(
                        loginRequest
                )
        );

        ApiResponseDTO<SentOtpData> response = ApiResponseDTO.success(
                "Otp Sent Successfully",
                sentOtpData
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<ApiResponseDTO<SigninResponseDTO>> verifyLogin(
            @Valid @RequestBody VerifyEmailOtpRequestDto verifyEmailOtpRequestDto,
            @CookieValue(value = CookieConstants.DEVICE_ID_COOKIE_NAME, required = false)
            String deviceId,
            @RequestHeader(value = CookieConstants.CLIENT_USER_AGENT, required = false)
            String userAgent,
            HttpServletRequest request
    ){

        String ip = request.getRemoteAddr();
        Validation.validate(ip, "ip", CLASS_NAME);

        tryConsume(
                VERIFY_LOGIN_IP_RATE_LIMITER_KEY + ip,
                verifyLoginIpBucketConfiguration,
                VERIFY_LOGIN_EMAIL_RATE_LIMITER_KEY + verifyEmailOtpRequestDto.getEmail(),
                verifyLoginEmailBucketConfiguration
        );

        if(deviceId == null || deviceId.isBlank())
            deviceId = UUID.randomUUID().toString();
        if(userAgent == null || userAgent.isBlank())
            userAgent = UserAgentStatus.NOT_AVAILABLE.name();

        SigninData signinData =
                this.emailLoginService.verifyLogin(
                        VerifyLoginByEmailModelMapper.from(
                                verifyEmailOtpRequestDto,
                                deviceId,
                                userAgent
                        )
                );

        return SigninResponseUtil.signinApiResponse(
                signinData,
                HttpStatus.OK,
                "Login Successful"
        );
    }

    private void tryConsume(
            String ipKey,
            BucketConfiguration ipConfiguration,
            String emailKey,
            BucketConfiguration emailConfiguration
    ){

        boolean ipAllowed = this.rateLimiterService.tryConsume(
                ipKey,
                ipConfiguration
        );

        if(ipAllowed){
            boolean emailAllowed = this.rateLimiterService.tryConsume(
                    emailKey,
                    emailConfiguration
            );
            if(emailAllowed)
                return;
        }

        throw new IllegalStateException("Rate Limit Exceeded");
    }
}