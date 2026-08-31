package com.project.staynest.auth.presentation.controller;

import com.project.staynest.auth.business.mapper.jwt.JwtUserDataMapper;
import com.project.staynest.auth.business.model.LatestTokenData;
import com.project.staynest.auth.business.service.JwtTokenService;
import com.project.staynest.auth.presentation.dto.response.wrapper.ApiResponseDTO;
import com.project.staynest.auth.presentation.util.ResponseCookieUtil;
import com.project.staynest.auth.security.model.JwtIdentity;
import com.project.staynest.auth.validation.Validation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class TokenController {


    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final JwtTokenService jwtTokenService;
    public TokenController(
            JwtTokenService jwtTokenService
    ){
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<Void>> refreshToken(Authentication authentication){

        JwtIdentity jwtIdentity = (JwtIdentity) authentication.getPrincipal();
        Validation.validate(jwtIdentity, "jwtIdentity", CLASS_NAME);

        LatestTokenData latestTokenData = this.jwtTokenService.rotateJwt(
                JwtUserDataMapper.from(jwtIdentity)
        );

        ResponseCookie refreshTokenCookie = ResponseCookieUtil.generateRefreshTokenCookie(
                latestTokenData.publicId(),
                latestTokenData.jwtGenerationResult().refreshToken(),
                latestTokenData.jwtGenerationResult().refreshTokenAge()
        );

        ResponseCookie activeUserPublicIdCookie = ResponseCookieUtil.generateActiveUserPublicIdCookie(
                latestTokenData.publicId(),
                latestTokenData.jwtGenerationResult().refreshTokenAge()
        );

        ResponseCookie deviceIdCookie = ResponseCookieUtil.generateDeviceIdCookie(
                latestTokenData.jwtGenerationResult().deviceId()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + latestTokenData.jwtGenerationResult().accessToken()
                )
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, activeUserPublicIdCookie.toString())
                .header(HttpHeaders.SET_COOKIE, deviceIdCookie.toString())
                .body(
                        ApiResponseDTO.success("Token Refreshed", null)
                );
    }
}