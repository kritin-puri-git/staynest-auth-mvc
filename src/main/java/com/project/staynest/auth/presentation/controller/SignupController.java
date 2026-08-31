package com.project.staynest.auth.presentation.controller;

import com.project.staynest.auth.business.mapper.signup.SignupModelMapper;
import com.project.staynest.auth.business.mapper.signup.VerifySignupModelMapper;
import com.project.staynest.auth.business.model.SentOtpData;
import com.project.staynest.auth.business.model.SigninData;
import com.project.staynest.auth.business.service.RateLimiterService;
import com.project.staynest.auth.business.service.SignupService;
import com.project.staynest.auth.constants.CookieConstants;
import com.project.staynest.auth.enums.UserAgentStatus;
import com.project.staynest.auth.presentation.dto.request.SignupRequestDTO;
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
public class SignupController {

    private static final String SIGNUP_IP_RATE_LIMITER_KEY = "signup:ip:";
    private static final String SIGNUP_EMAIL_RATE_LIMITER_KEY = "signup:email:";
    private static final String VERIFY_SIGNUP_IP_RATE_LIMITER_KEY = "verify-signup:ip:";
    private static final String VERIFY_SIGNUP_EMAIL_RATE_LIMITER_KEY = "verify-signup:email:";

    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final SignupService signupService;
    private final RateLimiterService rateLimiterService;
    private final BucketConfiguration signupIpBucketConfiguration;
    private final BucketConfiguration signupEmailBucketConfiguration;
    private final BucketConfiguration verifySignupIpBucketConfiguration;
    private final BucketConfiguration verifySignupEmailBucketConfiguration;
    public SignupController(
            SignupService signupService,
            RateLimiterService rateLimiterService,
            @Qualifier("signupIpBucketConfiguration")
            BucketConfiguration signupIpBucketConfiguration,
            @Qualifier("signupEmailBucketConfiguration")
            BucketConfiguration signupEmailBucketConfiguration,
            @Qualifier("verifySignupIpBucketConfiguration")
            BucketConfiguration verifySignupIpBucketConfiguration,
            @Qualifier("verifySignupEmailBucketConfiguration")
            BucketConfiguration verifySignupEmailBucketConfiguration
    ){
        this.signupService = signupService;
        this.rateLimiterService = rateLimiterService;
        this.signupIpBucketConfiguration = signupIpBucketConfiguration;
        this.signupEmailBucketConfiguration = signupEmailBucketConfiguration;
        this.verifySignupIpBucketConfiguration = verifySignupIpBucketConfiguration;
        this.verifySignupEmailBucketConfiguration = verifySignupEmailBucketConfiguration;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponseDTO<SentOtpData>> signUp(
            @Valid @RequestBody SignupRequestDTO dto,
            HttpServletRequest request
    ){

        String ip = request.getRemoteAddr();
        Validation.validate(ip, "ip", CLASS_NAME);

        tryConsume(
                SIGNUP_IP_RATE_LIMITER_KEY + ip,
                signupIpBucketConfiguration,
                SIGNUP_EMAIL_RATE_LIMITER_KEY + dto.getEmail(),
                signupEmailBucketConfiguration
        );
        SentOtpData sentOtpData = this.signupService.signup(
                SignupModelMapper.from(
                        dto
                )
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(
                        ApiResponseDTO.success(
                                "Otp Sent Successfully",
                                sentOtpData
                        )
                );
    }

    @PostMapping("/sign-up/verify-otp")
    public ResponseEntity<ApiResponseDTO<SigninResponseDTO>> verifySignup(
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
                VERIFY_SIGNUP_IP_RATE_LIMITER_KEY + ip,
                verifySignupIpBucketConfiguration,
                VERIFY_SIGNUP_EMAIL_RATE_LIMITER_KEY + verifyEmailOtpRequestDto.getEmail(),
                verifySignupEmailBucketConfiguration
        );

        if(deviceId == null || deviceId.isBlank())
            deviceId = UUID.randomUUID().toString();
        if(userAgent == null || userAgent.isBlank())
            userAgent = UserAgentStatus.NOT_AVAILABLE.name();

        SigninData signinData =
                this.signupService.verifySignup(
                        VerifySignupModelMapper.from(
                                verifyEmailOtpRequestDto,
                                deviceId,
                                userAgent
                        )
                );

        return SigninResponseUtil.signinApiResponse(
                signinData,
                HttpStatus.CREATED,
                "Account created"
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